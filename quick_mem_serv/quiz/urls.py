from rest_framework import routers

from .views import (
    CardViewSet,
    DeckViewSet,
    TagViewSet,
    QuizResultViewSet,
    StoreDeckViewSet
)

app_name = "quiz"

router = routers.DefaultRouter()
router.register(r"decks", DeckViewSet, basename="deck")
router.register(r"cards", CardViewSet, basename="card")
router.register(r"tags", TagViewSet, basename="tag")
router.register(r"quiz-results", QuizResultViewSet, basename="quiz_result")
router.register(r"store", StoreDeckViewSet, basename="store")

urlpatterns = router.urls
