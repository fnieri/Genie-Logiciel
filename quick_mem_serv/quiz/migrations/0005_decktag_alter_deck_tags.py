# Generated by Django 4.2 on 2023-04-30 14:04

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):
    dependencies = [
        ('quiz', '0004_alter_quizresult_card_and_more'),
    ]

    operations = [
        migrations.CreateModel(
            name='DeckTag',
            fields=[
                (
                    'id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('deck', models.ForeignKey(on_delete=models.CASCADE, related_name='deck_tags', to='quiz.deck')),
                ('tag', models.ForeignKey(on_delete=models.RESTRICT, related_name='deck_tags', to='quiz.tag')),
            ],
            options={
                'ordering': ['tag'],
                'unique_together': {('deck', 'tag')},
            },
        ),
        migrations.RemoveField(
            model_name='deck',
            name='tags',
        ),
        migrations.AddField(
            model_name='deck',
            name='tags',
            field=models.ManyToManyField(blank=True, related_name='decks', through='quiz.DeckTag', to='quiz.tag'),
        ),
    ]