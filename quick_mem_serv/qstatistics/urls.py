from django.contrib import admin
from django.shortcuts import redirect
from django.urls import path, include
from .views import AllStatsView

urlpatterns = [
    path('all/', AllStatsView.as_view(), name='all_stats'),
]
