from typing import Union, Iterable

from django.db import models
from django.contrib.auth import get_user_model
from django.db.models import Q
from rest_framework.exceptions import ValidationError

User = get_user_model()


class ModelPermissionMixin:
    def user_has_access(self, user):
        raise NotImplementedError()

    def queryset_for_user(self, user):
        raise NotImplementedError()


class DeckTag(models.Model):
    deck = models.ForeignKey('Deck', on_delete=models.CASCADE, related_name='deck_tags')
    tag = models.ForeignKey('Tag', on_delete=models.RESTRICT, related_name='deck_tags')

    class Meta:
        unique_together = ('deck', 'tag')
        ordering = ['tag']

    def __str__(self):
        return f'{self.deck.name} - {self.tag.name}'


class Deck(ModelPermissionMixin, models.Model):
    name = models.CharField(max_length=100)
    tags = models.ManyToManyField('Tag', through=DeckTag, related_name='decks', blank=True)
    users = models.ManyToManyField(User, related_name='decks')

    class Meta:
        ordering = ['name']

    def __str__(self):
        return self.name

    def user_has_access(self, user):
        return self.users.filter(pk=user.pk).exists()

    @classmethod
    def queryset_for_user(cls, user):
        return cls.objects.filter(users=user)


class StoreDeck(ModelPermissionMixin, models.Model):
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now_add=True)
    data = models.JSONField()
    deck = models.ForeignKey('Deck', on_delete=models.CASCADE)
    downloads = models.PositiveBigIntegerField(default=0)

    class Meta:
        ordering = ['created_at']

    def user_has_access(self, user):
        return True

    def queryset_for_user(self, user):
        return self.objects.all()

class Tag(models.Model):
    name = models.CharField(max_length=100, unique=True)

    class Meta:
        ordering = ['name']

    def __str__(self):
        return self.name

    def user_has_access(self, user):
        return True

    def queryset_for_user(self, user):
        return self.objects.all()


class Card(models.Model):
    question = models.CharField(max_length=100)
    answer = models.CharField(max_length=100)
    question_type = models.CharField(max_length=15, blank=True)
    answer_type = models.CharField(max_length=5, blank=True)
    deck = models.ForeignKey('Deck', on_delete=models.CASCADE, related_name='cards')

    class Meta:
        unique_together = ('question', 'deck')
        ordering = ['question']

    def __str__(self):
        return f"{self.question} - {self.answer} ({self.deck})"

    def user_has_access(self, user):
        return self.deck.users.filter(pk=user.pk).exists()

    def queryset_for_user(self, user):
        return self.objects.filter(deck__users=user)


class PerformanceType(models.IntegerChoices):
    VERY_BAD = -2
    BAD = -1
    MODERATE = 1
    GOOD = 2
    VERY_GOOD = 3

    @classmethod
    def get_choices(cls):
        return [(key.value, key.name) for key in cls]

    def is_mastered(self):
        return self.value in (self.GOOD, self.VERY_GOOD)

    def is_not_mastered(self):
        return not self.is_mastered()

    @classmethod
    def percentage_mastered(cls, performance_types: Iterable[Union[int, 'PerformanceType']]) -> float:
        mastered = []
        for performance_type in performance_types:
            if isinstance(performance_type, int):
                performance_type = cls(performance_type)

            mastered.append(performance_type.is_mastered())

        return mastered.count(True) / len(mastered) if mastered else 0

class QuizResult(models.Model):
    DEFAULT_PERFORMANCE_TYPE = 0
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='quiz_results')
    card = models.ForeignKey('Card', on_delete=models.CASCADE, related_name='results')
    performance_type = models.SmallIntegerField(choices=PerformanceType.get_choices())
    timestamp = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-timestamp']

    def __str__(self):
        return f"{self.user} {self.card} {self.performance_type} {self.timestamp}"

    def user_has_access(self, user):
        return self.user == user or self.card.deck.users.filter(pk=user.pk).exists()

    def queryset_for_user(self, user):
        return self.objects.filter(Q(user=user) | Q(card__deck__users=user))

    def clean(self):
        if not self.card.deck.users.filter(pk=self.user.pk).exists():
            raise ValidationError("User is not allowed to take this quiz")
