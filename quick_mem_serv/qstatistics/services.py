from collections import defaultdict
from datetime import timedelta, date
from itertools import groupby, count
from operator import attrgetter
from typing import Dict, Tuple, List

from django.db.models import Count, Q
from django.db.models.functions import TruncDay

from quiz.models import Deck, QuizResult, PerformanceType, Card
from django.db.models.query import QuerySet
from django.db.models import OuterRef, Subquery


class StudyAnalytics:
    def __init__(self, user):
        self._user = user
        self._session_threshold = timedelta(minutes=5)
        self._minimum_session_duration = timedelta(seconds=5)

        # Cache results
        self._results_by_day_deck = list(self._quiz_results_by_deck_and_day())
        self._study_count_by_deck = self._precompute_study_sessions()
        self._study_duration_by_deck = self._precompute_study_duration()
        self._last_card_results = self._fetch_last_card_results()

    def _precompute_study_duration(self) -> Dict[int, timedelta]:
        """
        Precompute the study duration for each deck.
        """
        study_duration = defaultdict(timedelta)

        for day, deck, group in self._results_by_day_deck:
            study_duration[deck.id] += self._calculate_study_sessions_duration(group)

        return study_duration

    def _fetch_last_card_results(self) -> Dict[int, List[PerformanceType]]:
        """
        Fetch the most recent QuizResult for each card in the user's decks.
        """
        last_card_results = defaultdict(list)

        # Fetch all cards in the user's decks
        cards = Card.objects.filter(deck__in=self._user.decks.all())

        # Fetch all QuizResults for this user
        quiz_results = QuizResult.objects.filter(user=self._user, card__in=cards).order_by('card_id', '-timestamp')

        # Group QuizResults by card_id and take the first (most recent) QuizResult for each card
        quiz_results_by_card = {
            card_id: next(group)
            for card_id, group in groupby(quiz_results, attrgetter('card_id'))
        }

        # Assign the most recent performance type for each card, or VERY_BAD if there's no QuizResult
        for card in cards:
            result = quiz_results_by_card.get(card.id)
            performance_type = result.performance_type if result is not None else PerformanceType.VERY_BAD
            last_card_results[card.deck_id].append(performance_type)

        return last_card_results

    def _precompute_study_sessions(self) -> Dict[int, int]:
        """
        Precompute the number of study sessions for each deck.
        """
        study_count = defaultdict(int)

        for day, deck, group in self._results_by_day_deck:
            study_count[deck.id] += len(self._split_into_sessions(group))

        return study_count

    def _split_into_sessions(self, quiz_results: List[QuizResult]) -> List[List[QuizResult]]:
        """
        Split a list of quiz results into a list of lists of quiz results.
        Each list of quiz results represents a study session.
        """

        if not quiz_results:
            return []

        if isinstance(quiz_results, QuerySet):
            quiz_results = list(quiz_results)

        quiz_results.sort(key=attrgetter('timestamp'))

        # Create a function to determine if a new session should start
        def is_new_session(result, _cache=[None]):
            last, _cache[0] = _cache[0], result
            return last is None or (result.timestamp - last.timestamp) > self._session_threshold

        sessions = []
        session = []

        for result in quiz_results:
            if is_new_session(result):
                if session:
                    sessions.append(session)
                session = [result]
            else:
                session.append(result)
        if session:
            sessions.append(session)

        return sessions

    def _calculate_study_sessions_duration(self, quiz_results) -> timedelta:
        """
        Calculate the duration of study sessions based on provided quiz results.
        A study session is defined as a sequence of quizzes where each is at most self._session_threshold (5 minutes)
        after the previous one.
        If there's only one quiz result for the day, the study session duration is considered as default_study_time.
        """
        sessions = self._split_into_sessions(quiz_results)

        total_study_time = timedelta()
        for session in sessions:
            session_duration = session[-1].timestamp - session[0].timestamp
            total_study_time += max(session_duration, self._minimum_session_duration)

        return total_study_time

    def _quiz_results_by_deck_and_day(self) -> Tuple[date, Deck, QuizResult]:
        """
        Get the quiz results grouped by deck and day.
        """
        quiz_results = QuizResult.objects.filter(user=self._user). \
            order_by('timestamp', 'card__deck_id').select_related('card__deck')

        for day, group in groupby(quiz_results, lambda result: result.timestamp.date()):
            for deck, deck_group in groupby(group, attrgetter('card.deck')):
                yield day, deck, list(deck_group)

    def get_total_study_duration(self) -> timedelta:
        """
        Get the total duration the user has spent studying across all decks.
        """
        return sum((self._calculate_study_sessions_duration(group) for day, deck, group in self._results_by_day_deck),
                   timedelta())

    def get_daily_study_duration(self) -> Dict[date, timedelta]:
        """
        Get the duration the user has spent studying for each day.
        """
        daily_study_duration = defaultdict(timedelta)

        for day, deck, group in self._results_by_day_deck:
            daily_study_duration[day] += self._calculate_study_sessions_duration(group)

        return daily_study_duration

    def get_mastery_level(self, deck):
        """
        Returns the precomputed mastery level for a deck.
        """
        last_card_results = self._last_card_results.get(deck.id, [])
        return PerformanceType.percentage_mastered(last_card_results)

    def get_deck_study_duration(self) -> Dict[int, timedelta]:
        """
        Get the precomputed study duration for each deck.
        """
        return self._study_duration_by_deck

    def get_study_duration_for_deck(self, deck) -> timedelta:
        """
        Get the study duration for a specific deck.
        """
        return self._study_duration_by_deck.get(deck.id, timedelta())

    def get_study_count(self) -> Dict[int, int]:
        """
        Get the number of study sessions for each deck.
        """
        return self._study_count_by_deck

    def get_study_count_for_deck(self, deck) -> int:
        """
        Get the number of study sessions for a specific deck.
        """
        return self._study_count_by_deck.get(deck.id, 0)

    def get_all_mastery_levels(self) -> Dict[int, float]:
        """
        Get the mastery level for all decks.
        """
        mastery_levels = {}

        for deck in Deck.objects.all():
            mastery_levels[deck.id] = self.get_mastery_level(deck)

        return mastery_levels

    def get_number_of_decks_studied(self):
        return len([deck for deck in self._study_count_by_deck if self._study_count_by_deck[deck] > 0])

    def get_decks_sorted_by_mastery_level(self):
        """
        Get all decks sorted by their mastery level.
        """
        mastery_levels = self.get_all_mastery_levels()
        return sorted(mastery_levels, key=mastery_levels.get, reverse=True)

    def get_mastery_level_by_deck_per_day(self, deck: Deck) -> Dict[str, int]:
        """
        Get the mastery level for a specific deck per day.
        """
        mastery_level_by_day = {}

        quiz_results = QuizResult.objects.filter(
            user=self._user,
            card__deck=deck
        ).annotate(
            day=TruncDay('timestamp')
        ).values(
            'day'
        ).annotate(
            good_performance=Count('performance_type', filter=Q(performance_type=PerformanceType.GOOD)),
            very_good_performance=Count('performance_type', filter=Q(performance_type=PerformanceType.VERY_GOOD))
        )

        for result in quiz_results:
            total_performance = result['good_performance'] + result['very_good_performance']
            card_count = deck.cards.count()

            if card_count == 0 or total_performance == 0:
                mastery_level_by_day[result['day'].strftime('%Y-%m-%d')] = 0.0
                continue

            percentage = min(total_performance / card_count, 1.0)
            mastery_level_by_day[result['day'].strftime('%Y-%m-%d')] = float(percentage)

        return mastery_level_by_day


    def get_study_count_for_deck_per_day(self, deck: Deck) -> Dict[str, int]:
        """
        Get the number of study sessions for a specific deck per day.
        """

        study_count_by_day = defaultdict(int)

        quiz_results = QuizResult.objects.filter(
            user=self._user,
            card__deck=deck
        ).order_by('timestamp')

        sessions = self._split_into_sessions(quiz_results)

        for session in sessions:
            start_date = session[0].timestamp.date()
            study_count_by_day[start_date.strftime('%Y-%m-%d')] += 1

        return study_count_by_day





