# Generated by Django 4.2 on 2023-05-10 18:47

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('quiz', '0009_storedeck_updated_at'),
    ]

    operations = [
        migrations.AddField(
            model_name='storedeck',
            name='downloads',
            field=models.PositiveBigIntegerField(default=0),
        ),
        migrations.DeleteModel(
            name='StoreDeckDownloads',
        ),
    ]
