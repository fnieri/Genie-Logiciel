from rest_framework import viewsets, mixins
from django.db.models import Q, Count
from qstatistics.services import StudyAnalytics
from .models import Card, Deck, QuizResult, Tag
from django.contrib.auth.models import Group, User
from django.db.models import Q
from rest_framework import permissions, viewsets, mixins
from rest_framework.decorators import action

from .models import Card, Deck, QuizResult, Tag, StoreDeck
from .serializers import (
    CardSerializer,
    DeckSerializer,
    QuizResultSerializer,
    TagSerializer,
    StoreDeckSerializer,
)
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework import filters
from django.http import JsonResponse
from rest_framework.response import Response


class DeckViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows decks to be viewed or edited.
    """

    serializer_class = DeckSerializer
    filter_backends = [
        DjangoFilterBackend,
        filters.SearchFilter,
        filters.OrderingFilter,
    ]
    filterset_fields = ["name", "tags__name"]
    search_fields = ["name", "tags__name"]
    ordering_fields = ["name", "tags__name", "quiz_results_count"]

    def get_study_analytics(self, _study_analytics=None):
        if _study_analytics is None:
            _study_analytics = StudyAnalytics(self.request.user)
        return _study_analytics

    def get_serializer_context(self):
        return {"study_analytics": self.get_study_analytics(), "request": self.request}

    def get_queryset(self):
        """
        This view should return a list of all the decks
        for the currently authenticated user.
        """

        return Deck.queryset_for_user(self.request.user).annotate(
            quiz_results_count=Count(
                "cards__results", filter=Q(cards__results__user=self.request.user)
            )
        )


class StoreDeckViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows store decks to be viewed or edited.
    """

    serializer_class = StoreDeckSerializer
    filter_backends = [filters.SearchFilter, filters.OrderingFilter]
    search_fields = ["data__name"]
    ordering_fields = ["data__name"]

    def get_study_analytics(self, _study_analytics=None):
        if _study_analytics is None:
            _study_analytics = StudyAnalytics(self.request.user)
        return _study_analytics

    def get_serializer_context(self):
        return {"study_analytics": self.get_study_analytics(), "request": self.request}

    def get_queryset(self):
        return StoreDeck.objects.all()

    def list(self, request, *args, **kwargs):
        decks_in_store = super().list(request, *args, **kwargs)

        popular_decks = StoreDeck.objects.order_by("downloads")[:12]
        serializer = self.get_serializer(popular_decks, many=True)
        return Response(
            {"populars": serializer.data, "decks": decks_in_store.data}, status=200
        )

    @action(detail=True, methods=["POST"])
    def download(self, request, *args, **kwargs):
        # Remove Deck Id from data and update users
        deck_data = self.get_object().data
        deck_data.pop("id")
        deck_data["users"] = [self.request.user]

        # Remove Card Id and Deck Id from cards data
        cards_data = map(
            lambda card: {
                key: value for key, value in card.items() if key not in ["id", "deck"]
            },
            deck_data.pop("cards", []),
        )

        # Create new Deck based on data
        deck_serializer = DeckSerializer(
            data=deck_data, context=self.get_serializer_context()
        )
        if deck_serializer.is_valid():
            deck = deck_serializer.save()

            # Create new Cards for new Deck based on data
            cards_data = list(map(lambda card: {**card, "deck": deck.id}, cards_data))
            cards_serializer = CardSerializer(
                data=cards_data, many=True, context=self.get_serializer_context()
            )
            if cards_serializer.is_valid():
                cards_serializer.save()

                # Increment downloads count for this deck
                store_deck = self.get_object()
                store_deck.downloads += 1
                store_deck.save()

                ret = {**deck_serializer.data, "cards": cards_serializer.data}
                return Response(ret, status=201)
            else:
                return Response(cards_serializer.errors, status=400)
        else:
            return Response(deck_serializer.errors, status=400)


class CardViewSet(viewsets.ModelViewSet):
    """
    API endpoint that allows cards to be viewed or edited.
    """

    serializer_class = CardSerializer
    filter_backends = [
        DjangoFilterBackend,
        filters.SearchFilter,
        filters.OrderingFilter,
    ]
    filterset_fields = ["question", "answer", "deck__name"]
    search_fields = ["question", "answer", "deck__name"]
    ordering_fields = ["question", "answer", "deck__name"]

    def get_serializer_context(self):
        return {"request": self.request}

    def get_queryset(self):
        """
        This view should return a list of all the cards
        for the currently authenticated user.
        """
        return Card.objects.filter(deck__users=self.request._user)


class TagViewSet(
    mixins.CreateModelMixin,
    mixins.ListModelMixin,
    mixins.RetrieveModelMixin,
    mixins.DestroyModelMixin,
    viewsets.GenericViewSet,
):
    """
    API endpoint that allows tags to be viewed or edited.
    """

    queryset = Tag.objects.all()
    serializer_class = TagSerializer
    filter_backends = [filters.SearchFilter, filters.OrderingFilter]
    search_fields = ["name"]
    ordering_fields = ["name"]


class QuizResultViewSet(
    mixins.CreateModelMixin,
    mixins.ListModelMixin,
    mixins.RetrieveModelMixin,
    viewsets.GenericViewSet,
):
    """
    API endpoint that allows quiz results to be viewed or edited.
    """

    serializer_class = QuizResultSerializer
    filter_backends = [
        DjangoFilterBackend,
        filters.SearchFilter,
        filters.OrderingFilter,
    ]
    filterset_fields = ["card__deck__name", "performance_type", "timestamp"]
    search_fields = [
        "card__deck__name",
        "performance_type",
        "card__question",
        "card__answer",
        "card__deck__tags__name",
    ]
    ordering_fields = ["card__deck__name", "performance_type", "timestamp"]

    def get_serializer_context(self):
        return {"request": self.request}

    def get_queryset(self):
        """
        This view should return a list of all the quiz results
        for the currently authenticated user.
        """
        return QuizResult.objects.filter(user=self.request._user)
