# EduChallenge

Plateforme sociale de defis educatifs gamifies.

EduChallenge est une application interactive qui encourage les jeunes a apprendre a travers des defis, des quiz et des mecanismes de progression. La plateforme combine apprentissage, participation sociale et competition amicale pour rendre l'experience plus motivante.

Le coeur du produit suit une boucle simple :

`Learning -> Challenge -> Participation -> Achievement -> Motivation`

## Vision

EduChallenge transforme l'apprentissage en experience communautaire. Un utilisateur ne consomme pas seulement du contenu : il cree des defis, participe a ceux des autres, progresse, gagne des points, debloque des badges et apparait dans les classements.

## Fonctionnalites principales

- Creation de defis educatifs par les utilisateurs
- Participation aux defis d'autres utilisateurs
- Gestion de tentatives et reponses QCM
- Attribution de points, niveaux et badges
- Classements et suivi de progression
- Notifications d'activite et de recompenses
- Gestion des utilisateurs et de la securite de base

## Acteurs principaux

### Student / User

Le `Student` ou `User` est l'acteur central de la plateforme. Il utilise EduChallenge comme un espace d'apprentissage interactif, social et stimulant.

Ses objectifs principaux :

- apprendre via des defis educatifs
- participer a une communaute active
- se mesurer aux autres de maniere positive
- suivre sa progression et ses accomplissements
- rester motive grace a la gamification

Actions typiques :

- s'inscrire et se connecter
- creer un defi
- consulter et tenter des defis
- repondre a des questions QCM
- gagner des points et des badges
- suivre son rang dans le leaderboard

### Administrator

L'`Administrator` supervise l'ensemble de la plateforme afin de garantir un environnement sain, fiable et coherent.

Responsabilites principales :

- maintenir le bon fonctionnement global du systeme
- assurer la qualite du contenu
- faire respecter les regles de la communaute
- intervenir sur les defis lorsque necessaire
- garantir la stabilite et la securite de la plateforme

Dans l'etat actuel du projet, le role `ADMIN` existe deja au niveau backend pour certaines operations de gestion des defis.

## Parcours fonctionnel

1. Un utilisateur apprend ou maitrise un sujet.
2. Il transforme ce savoir en defi educatif.
3. D'autres utilisateurs participent a ce defi.
4. Les participants gagnent des points, badges ou progression.
5. Ces recompenses renforcent la motivation et encouragent une nouvelle participation.

## Architecture actuelle

Le depot est organise en deux parties principales :

- `frontend/` : interface web en `React 19` avec `Vite`
- `backend/` : API en `Spring Boot`, `Java 21`, `Spring Data JPA` et `PostgreSQL`

Modules metier deja presents dans le code :

- `auth` : inscription et connexion
- `users` : profil utilisateur, role, niveau, points, streak
- `challenges` : creation, consultation, details et tentatives
- `gamification` : leaderboard, badges, notifications, activite recente

## Modele de donnees metier

Le backend couvre deja les concepts essentiels de la plateforme :

- `users`
- `challenges`
- `challenge_steps`
- `step_options`
- `challenge_attempts`
- `attempt_answers`
- `badges`
- `user_badges`
- `notifications`

Le script SQL [backend/sql/2026-04-19_add_qcm_tables.sql](backend/sql/2026-04-19_add_qcm_tables.sql) etend la plateforme vers un modele QCM complet pour les defis.

## Demarrage rapide

### Base de donnees

Le projet fournit un `docker-compose.yml` avec PostgreSQL :

```bash
docker compose up -d
```

Configuration actuelle :

- base : `educhallenge`
- utilisateur : `postgres`
- mot de passe : `1234`
- port hote : `5433`

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Le backend utilise par defaut :

- `jdbc:postgresql://localhost:5433/educhallenge`
- port HTTP par defaut de Spring Boot : `8080`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

L'API frontend pointe par defaut vers :

- `http://localhost:8080`

## Objectif produit

EduChallenge vise a construire un ecosysteme ou apprendre devient social, mesurable et engageant. La plateforme cherche moins a distribuer du contenu passif qu'a encourager l'action : creer, participer, reussir et recommencer.
