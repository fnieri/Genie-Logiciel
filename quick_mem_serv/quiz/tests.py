from django.contrib.auth import get_user_model
from django.urls import reverse
from rest_framework import status
from rest_framework.test import APITestCase

from .models import Deck, Tag, Card, QuizResult
from .serializers import DeckSerializer

User = get_user_model()


class ModelTestCase(APITestCase):
    def setUp(self):
        self.user = User.objects.create_user(username="testuser", password="testpassword")

    def test_create_deck(self):
        deck = Deck.objects.create(name="Test Deck")
        deck.users.add(self.user)
        deck.save()

        self.assertEqual(deck.name, "Test Deck")
        self.assertTrue(self.user in deck.users.all())

    def test_create_tag(self):
        tag = Tag.objects.create(name="Test Tag")

        self.assertEqual(tag.name, "Test Tag")

    def test_create_card(self):
        deck = Deck.objects.create(name="Test Deck")
        deck.users.add(self.user)
        deck.save()

        card = Card.objects.create(question="Test Question", answer="Test Answer", deck=deck)

        self.assertEqual(card.question, "Test Question")
        self.assertEqual(card.answer, "Test Answer")
        self.assertEqual(card.deck, deck)

    def test_create_quiz_result(self):
        deck = Deck.objects.create(name="Test Deck")
        deck.users.add(self.user)
        deck.save()

        card = Card.objects.create(question="Test Question", answer="Test Answer", deck=deck)

        quiz_result = QuizResult.objects.create(user=self.user, card=card, performance_type=2)

        self.assertEqual(quiz_result._user, self.user)
        self.assertEqual(quiz_result.card, card)
        self.assertEqual(quiz_result.performance_type, 2)

    def test_add_tag_to_deck(self):
        deck = Deck.objects.create(name="Test Deck")
        deck.users.add(self.user)
        deck.save()

        tag = Tag.objects.create(name="Test Tag")
        deck.tags.add(tag)
        deck.save()

        self.assertTrue(tag in deck.tags.all())

    def test_remove_tag_from_deck(self):
        deck = Deck.objects.create(name="Test Deck")
        deck.users.add(self.user)
        deck.save()

        tag = Tag.objects.create(name="Test Tag")
        deck.tags.add(tag)
        deck.save()

        deck.tags.remove(tag)
        deck.save()

        self.assertFalse(tag in deck.tags.all())


class APITestCase(APITestCase):
    def setUp(self):
        self.user = User.objects.create_user(username="testuser", password="testpassword")
        self.client.login(username="testuser", password="testpassword")

    def test_deck_creation(self):
        url = reverse('quiz:deck-list')
        data = {"name": "Test Deck"}
        response = self.client.post(url, data, format='json')

        # Should fail because we didn't specify the users
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)

        data = {"name": "Test Deck", "users": [self.user.username]}
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_201_CREATED)

        self.assertEqual(Deck.objects.count(), 1)
        self.assertEqual(Deck.objects.get().name, 'Test Deck')

    def test_card_creation(self):
        deck = Deck.objects.create(name="Test Deck")
        deck.users.add(self.user)
        deck.save()

        url = reverse('quiz:card-list')
        data = {"question": "Test Question", "answer": "Test Answer", "deck": deck.id}
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Card.objects.count(), 1)
        self.assertEqual(Card.objects.get().question, 'Test Question')

    def test_tag_creation(self):
        url = reverse('quiz:tag-list')
        data = {"name": "Test Tag"}
        response = self.client.post(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        self.assertEqual(Tag.objects.count(), 1)
        self.assertEqual(Tag.objects.get().name, 'Test Tag')

    def test_add_tag_to_deck(self):
        tag = Tag.objects.create(name="Test Tag")
        deck = Deck.objects.create(name="Test Deck")
        deck.users.add(self.user)
        deck.save()

        url = reverse('quiz:deck-detail', args=[deck.pk])
        data = DeckSerializer(deck).data
        data["tags"].append(tag.name)
        response = self.client.patch(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        deck.refresh_from_db()
        self.assertTrue(tag in deck.tags.all())

    def test_remove_tag_from_deck(self):
        tag = Tag.objects.create(name="Test Tag")
        deck = Deck.objects.create(name="Test Deck")
        deck.users.add(self.user)
        deck.tags.add(tag)
        deck.save()

        url = reverse('quiz:deck-detail', args=[deck.pk])
        data = DeckSerializer(deck).data
        data["tags"].remove(tag.name)
        response = self.client.patch(url, data, format='json')

        self.assertEqual(response.status_code, status.HTTP_200_OK)
        deck.refresh_from_db()
        self.assertFalse(tag in deck.tags.all())

