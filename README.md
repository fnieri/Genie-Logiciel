# Project

This is the project for the ULB Third year course - INFO-F307 Génie Logiciel (Software Engineering)
It is a Quizlet-like application built on Java, with JavaFX for the frontend and Django-REST framework for the server.

# Flashcards
Flashcards est une application d’aide à l’étude basée sur un système de flashcards. Les flashcards sont un outil de révision simple et efficace ; au recto de la carte est inscrit une question et au verso la réponse à la question. L'utilisateur peut alors parcourir les questions et tester se connaissances grâce à ces cartes.

## Utilisation
Pour utiliser ce logiciel, vous pouvez:
  >Lancer la classe Main qui se trouve dans 2023-groupe-4/src/ulb/infof307/g04 après avoir chargé les dépendances de Maven qui se trouvent dans le fichier pom.xml.

  >Telecharger le .jar correspondant à l'iteration courante qui se trouve dans 2023-groupe-4/dist et le lancer.

### Histoires implementées par iteration

#### Iteration 1
- [x] Histoire 1 - Gestion des comptes utilisateurs
- [x] Histoire 3 - Création de cartes dans un paquet

#### Iteration 2
- [x] Histoire 2 - Gestion des paquets de cartes
- [x] Histoire 4 - Étudier un jeu de cartes
- [x] Histoire 6 - Modes de jeu

#### Iteration 3
- [x] Histoire 10 - Connexion a un serveur
- [x] Histoire 15 - Import/export de jeux de cartes
- [x] Histoire 16 - Impression de jeux de cartes
- [x] Histoire 17 - Latex et html dans les cartes

#### Iteration 4
- [ ] Histoire 8 - Question de differents types
- [ ] Histoire 9 - Statistiques d'entrainement
- [ ] Histoire 11 - Store de jeux de cartes 
- [ ] Histoire 14 - Lecture audio


Pour exécuter le serveur, allez dans quick_mem_serv et exécutez Docker Compose avec les commandes suivantes:

```shell
cd quick_mem_serv
docker-compose up --build
```

Par défaut le serveur est accessible sur le port 8000 avec le username "admin" et le mot de passe "password".

Documentation de l'API du serveur:
(Il faut être connecté avec son compte pour accéder à la documentation)
https://flashcards.ulb.lucapalmi.com/doc/
