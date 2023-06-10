
from django.contrib import admin
from django.urls import path, include
from .views import RegisterView

from .views import ChangePasswordView

app_name = "qauth"

urlpatterns = [
    path("register/", RegisterView.as_view(), name="register"),
    path("change-password/", ChangePasswordView.as_view(), name="change-password")
]
