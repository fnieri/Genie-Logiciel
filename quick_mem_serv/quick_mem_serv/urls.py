"""quick_mem_serv URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.shortcuts import redirect
from django.urls import path, include
from rest_framework.schemas import get_schema_view
from django.views.generic import TemplateView

from django.conf import settings

urlpatterns = [
    path("api_schema/", get_schema_view(title="API Schema", description="Guide for the REST API"), name='api_schema'),
    path('doc/', TemplateView.as_view(
        template_name='docs.html',
        extra_context={'schema_url': 'api_schema'}), name='swagger-ui'),
    path("admin/", admin.site.urls),
    path("api/quiz/", include("quiz.urls")),
    path('', lambda request: redirect('api/quiz/', permanent=False)),

    path("api/auth/", include("rest_framework.urls")),
    path("api/auth/", include("qauth.urls")),
    path("api/stats/", include("qstatistics.urls")),
]
