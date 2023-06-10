from django.contrib import admin

from .models import Deck, Tag, QuizResult, Card, StoreDeck


class CardInline(admin.TabularInline):
    model = Card
    extra = 1


class TagInline(admin.TabularInline):
    model = Tag.decks.through
    extra = 1

@admin.register(Tag)
class TagAdmin(admin.ModelAdmin):
    list_display = ('name',)
    search_fields = ('name',)
    #inlines = [DeckInline] //FIXME Shall we add this? Remove this?

@admin.register(Deck)
class DeckAdmin(admin.ModelAdmin):
    list_display = ('name',)
    inlines = [CardInline, TagInline]
    search_fields = ('name',)
    list_filter = ('tags',)

@admin.register(StoreDeck)
class StoreDeckAdmin(admin.ModelAdmin):
    list_display = ('id', 'created_at', 'updated_at', 'deck', 'downloads')

@admin.register(Card)
class CardAdmin(admin.ModelAdmin):
    list_display = ('question', 'answer')
    search_fields = ('question', 'answer')
    list_editable = ('answer',)
    list_filter = ('deck',)


@admin.register(QuizResult)
class QuizResultAdmin(admin.ModelAdmin):
    list_display = ('user', 'card', 'performance_type', 'timestamp')
    search_fields = ('user__username', 'card__question')
    list_filter = ('user', 'card', 'performance_type', 'timestamp', 'card__deck__name')

