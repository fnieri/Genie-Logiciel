import os
from django.core.management.utils import get_random_secret_key

from .settings import *

load_dotenv()

DEBUG = False

ALLOWED_HOSTS = ["0.0.0.0", "127.0.0.1"]  # TODO: Change this to the domain name of our website
if 'DOMAIN_NAME' in os.environ:
    ALLOWED_HOSTS.append(os.environ["DOMAIN_NAME"])

CSRF_TRUSTED_ORIGINS = ["http://0.0.0.0", "http://127.0.0.1"]
if 'CSRF_TRUSTED_ORIGIN' in os.environ:
    CSRF_TRUSTED_ORIGINS.append(os.environ["CSRF_TRUSTED_ORIGIN"])

SECRET_KEY = os.environ.get("SECRET_KEY", get_random_secret_key())

DATABASES = {
    "default": {
        "ENGINE": "django.db.backends.postgresql",
        "NAME": os.environ["POSTGRES_NAME"],
        "USER": os.environ["POSTGRES_USER"],
        "HOST": os.environ["POSTGRES_HOST"],
        "PASSWORD": os.environ["POSTGRES_PASSWORD"],
    }
}

LOGS_DIR = os.environ.get("LOGS_DIR", "/var/log/quick_mem_serv")

LOGGING = {
    'version': 1,
    'disable_existing_loggers': False,
    'filters': {
        'require_debug_false': {
            '()': 'django.utils.log.RequireDebugFalse',
        }
    },
    'handlers': {
        'logfile': {
            'level': 'INFO',
            'class': 'logging.handlers.RotatingFileHandler',
            'filename': os.path.join(LOGS_DIR, 'quick_mem_serv.log'),
            'maxBytes': 1024 * 1024 * 5,  # 5 MB
            'backupCount': 5,
        },
    },
    'loggers': {
        'django': {
            'handlers': ['logfile'],
            'level': 'INFO',
            'propagate': False,
        },
    },
    'formatters': {
        'verbose': {
            'format': '%(levelname)s %(asctime)s %(pathname)s %(funcName)s %(lineno)d %(message)s'
        },
    },

}

USE_X_FORWARDED_HOST = True
USE_X_FORWARDED_PORT = True
