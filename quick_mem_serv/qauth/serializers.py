from django.contrib.auth import get_user_model
from rest_framework import serializers
from django.contrib.auth.password_validation import validate_password

User = get_user_model()


class RegisterSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, required=True, style={'input_type': 'password'},
                                     validators=[validate_password])

    class Meta:
        model = User
        fields = ["username", "password"]

    def create(self, validated_data):
        return User.objects.create_user(**validated_data)


class ChangePasswordSerializer(serializers.Serializer):
    old_password = serializers.CharField(required=True, style={'input_type': 'password'})
    new_password = serializers.CharField(required=True, style={'input_type': 'password'},
                                         validators=[validate_password])

    def validate_old_password(self, value):
        user = self.context["request"]._user
        if not user.check_password(value):
            raise serializers.ValidationError("Old Password is not correct !")
        else:
            return value

    def save(self, **kwargs):
        new_password = self.validated_data["new_password"]
        user = self.context["request"]._user
        user.set_password(new_password)
        user.save()
        return user
