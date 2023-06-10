from django.contrib.auth import get_user_model
from .models import Deck, Tag, Card, QuizResult, StoreDeck
from rest_framework import serializers

import datetime as dt

User = get_user_model()


class TagSerializer(serializers.ModelSerializer):
    class Meta:
        model = Tag
        fields = ["id", "name"]


class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ["username"]


class CardSerializer(serializers.ModelSerializer):
    mastery_level = serializers.SerializerMethodField()

    class Meta:
        model = Card
        fields = [
            "id",
            "question",
            "question_type",
            "answer",
            "answer_type",
            "deck",
            "mastery_level",
        ]

    def validate(self, data):
        if not data["deck"].user_has_access(self.context["request"].user):
            raise serializers.ValidationError(
                f"The user {self.context['request'].user} has to be in deck.users"
            )
        return data

    def get_mastery_level(self, obj):
        try:
            quiz_result = QuizResult.objects.filter(card=obj).latest("performance_type")
            return quiz_result.performance_type
        except QuizResult.DoesNotExist:
            return QuizResult.DEFAULT_PERFORMANCE_TYPE


class DeckSerializer(serializers.ModelSerializer):
    tags = serializers.SlugRelatedField(
        slug_field="name", queryset=Tag.objects.all(), many=True, required=False
    )
    users = serializers.SlugRelatedField(
        slug_field="username", queryset=User.objects.all(), many=True
    )
    cards = CardSerializer(many=True, read_only=True)
    study_sessions = serializers.SerializerMethodField()
    mastery_level = serializers.SerializerMethodField()
    study_duration = serializers.SerializerMethodField()
    mastery_level_by_day = serializers.SerializerMethodField()
    study_sessions_by_day = serializers.SerializerMethodField()

    class Meta:
        model = Deck
        fields = ["id", "name", "tags", "users", "cards", "study_sessions", "mastery_level", "study_duration", "mastery_level_by_day", "study_sessions_by_day"]

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.study_analytics = None
        if "context" in kwargs and "study_analytics" in kwargs["context"]:
            self.study_analytics = kwargs["context"]["study_analytics"]

    def get_study_sessions(self, obj):
        if self.study_analytics:
            return self.study_analytics.get_study_count_for_deck(obj)
        else:
            raise serializers.ValidationError(
                "A study_analytics instance must be provided to calculate study sessions."
            )

    def get_mastery_level(self, obj):
        if self.study_analytics:
            return self.study_analytics.get_mastery_level(obj)
        else:
            raise serializers.ValidationError(
                "A study_analytics instance must be provided to calculate the mastery level."
            )

    def get_mastery_level_by_day(self, obj):
        if self.study_analytics:
            return self.study_analytics.get_mastery_level_by_deck_per_day(obj)
        else:
            raise serializers.ValidationError(
                "A study_analytics instance must be provided to calculate the mastery level by day.")

    def get_study_sessions_by_day(self, obj):
        if self.study_analytics:
            return self.study_analytics.get_study_count_for_deck_per_day(obj)
        else:
            raise serializers.ValidationError(
                "A study_analytics instance must be provided to calculate study sessions by day.")

    def get_study_duration(self, obj):
        if self.study_analytics:
            return self.study_analytics.get_study_duration_for_deck(obj)
        else:
            raise serializers.ValidationError(
                "A study_analytics instance must be provided to calculate study duration."
            )

    def validate(self, data):
        if self.context["request"].user not in data.get("users", []):
            raise serializers.ValidationError(
                f"The user {self.context['request'].user} has to be in users"
            )
        return data

    def create(self, validated_data):
        users = validated_data.pop("users", [])

        # Add the current user if there are no users
        if not users:
            users.append(self.context["request"].user)

        tags = validated_data.pop("tags", [])

        deck = Deck.objects.create(**validated_data)

        deck.users.set(users)
        deck.tags.set(tags)

        return deck


class StoreDeckSerializer(serializers.ModelSerializer):
    class Meta:
        model = StoreDeck
        fields = ["id", "data", "created_at", "updated_at", "deck", "downloads"]
        read_only_fields = ["data", "created_at", "updated_at", "downloads"]

    def create(self, validated_data):
        deck: Deck = validated_data["deck"]
        data = DeckSerializer(deck, many=False, context=self.context).data

        # Remove fields that should not be saved in store
        data.pop("study_sessions")
        data.pop("mastery_level")
        data.pop("study_duration")

        # Check if deck already exists in store, and update.
        # if not, publish deck in store
        store_deck = StoreDeck.objects.filter(deck=deck).first()
        if store_deck is None:
            store_deck = StoreDeck.objects.create(deck=deck, data=data)
        else:
            store_deck.updated_at = dt.datetime.utcnow()
            store_deck.data = data
            store_deck.save()

        return store_deck


class QuizResultSerializer(serializers.ModelSerializer):
    card = serializers.PrimaryKeyRelatedField(queryset=Card.objects.all())

    class Meta:
        model = QuizResult
        fields = ["id", "card", "performance_type", "timestamp"]

    def __init__(self, *args, **kwargs):
        super(QuizResultSerializer, self).__init__(*args, **kwargs)
        self.fields["card"].queryset = Card.objects.filter(
            deck__users=self.context["request"].user
        )

    def get_performance_type(self, obj):
        return obj.get_performance_type_display()

    def validate(self, data):
        if not data["card"].user_has_access(self.context["request"].user):
            raise serializers.ValidationError("User is not allowed to take this quiz")
        return data

    def create(self, validated_data):
        validated_data["user"] = self.context["request"].user
        return super().create(validated_data)
