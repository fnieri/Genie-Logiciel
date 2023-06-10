from django.shortcuts import render
from rest_framework.response import Response
from rest_framework.views import APIView

from quiz.models import Deck
from .services import StudyAnalytics


from .serializers import StudyAnalyticsSerializer

class AllStatsView(APIView):
    def get(self, request):
        study_analytics = StudyAnalytics(request._user)

        data = {
            'total_study_duration': study_analytics.get_total_study_duration(),
            'daily_study_duration': study_analytics.get_daily_study_duration(),
            'deck_study_counts': study_analytics.get_study_count(),
            'number_of_decks_studied': study_analytics.get_number_of_decks_studied(),
        }

        serializer = StudyAnalyticsSerializer(data=data)

        if serializer.is_valid():
            return Response(serializer.validated_data)
        else:
            return Response(serializer.errors, status=400)
