from datetime import timedelta

from rest_framework import serializers

from quiz.serializers import DeckSerializer


class StudyAnalyticsSerializer(serializers.Serializer):
    total_study_duration = serializers.DurationField()
    daily_study_duration = serializers.DictField(child=serializers.DurationField())
    deck_study_counts = serializers.DictField(child=serializers.IntegerField())
    number_of_decks_studied = serializers.IntegerField()
