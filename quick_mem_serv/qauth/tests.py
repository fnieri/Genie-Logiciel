from django.contrib.auth import get_user_model
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APIClient, APITestCase

User = get_user_model()


class RegisterViewTestCase(APITestCase):
    def setUp(self):
        self.client = APIClient()
        self.url = reverse('qauth:register')
        self.user_data = {
            "username": "testuser",
            "email": "testuser@example.com",
            "password": "Test12345!",
            "password2": "Test12345!"
        }

    def test_register_new_user(self):
        response = self.client.post(self.url, self.user_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(User.objects.count(), 1)
        self.assertEqual(User.objects.first().username, self.user_data['username'])

    def test_register_existing_user(self):
        User.objects.create_user(self.user_data['username'], self.user_data['email'], self.user_data['password'])
        response = self.client.post(self.url, self.user_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)


class ChangePasswordViewTestCase(APITestCase):
    def setUp(self):
        self.client = APIClient()
        self.url = reverse('qauth:change-password')
        self.user = User.objects.create_user('testuser', 'testuser@example.com', 'Test12345!')
        self.client.force_authenticate(user=self.user)
        self.password_data = {
            "old_password": "Test12345!",
            "new_password": "NewTest12345!"
        }

    def test_change_password_success(self):
        response = self.client.put(self.url, self.password_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_200_OK)
        self.assertTrue(User.objects.first().check_password(self.password_data['new_password']))

    def test_change_password_wrong_old_password(self):
        wrong_password_data = {
            "old_password": "WrongOldPassword",
            "new_password": "NewTest12345!"
        }
        response = self.client.put(self.url, wrong_password_data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertFalse(User.objects.first().check_password(wrong_password_data['new_password']))
        self.assertTrue(User.objects.first().check_password(self.password_data['old_password']))
