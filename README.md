# EduChallenge

Plateforme sociale de defis educatifs gamifies.

EduChallenge est une application web qui encourage l'apprentissage par des defis, des quiz et des mecanismes de progression. Le produit combine participation sociale, competition amicale et gamification.

Le coeur du produit suit une boucle simple :

`Learning -> Challenge -> Participation -> Achievement -> Motivation`

## Vision

EduChallenge transforme l'apprentissage en experience communautaire. Un utilisateur ne consomme pas seulement du contenu : il cree des defis, participe a ceux des autres, progresse, gagne des points, debloque des badges et apparait dans les classements.

## Fonctionnalites principales

- Inscription et connexion utilisateur
- Creation de defis educatifs
- Participation a des defis QCM
- Gestion des tentatives et des reponses
- Attribution de points, niveaux et badges
- Leaderboard et suivi de progression
- Notifications d'activite
- Role `ADMIN` deja present au backend

## Architecture applicative

Le depot est organise en deux parties principales :

- `frontend/` : interface web en `React 19` avec `Vite`
- `backend/` : API `Spring Boot`, `Java 21`, `Spring Data JPA` et `PostgreSQL`

Modules metier deja presents dans le code :

- `auth` : inscription, connexion, JWT
- `users` : profil, role, niveau, points
- `challenges` : creation, consultation, tentatives
- `gamification` : leaderboard, badges, activite recente

## Modele de donnees

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

Le script SQL [backend/sql/2026-04-19_add_qcm_tables.sql](backend/sql/2026-04-19_add_qcm_tables.sql) etend la plateforme vers un modele QCM complet.

## Stack technique

- Frontend : `React 19`, `Vite`, `Nginx`
- Backend : `Spring Boot`, `Java 21`, `Maven`
- Base de donnees : `PostgreSQL 16`
- Conteneurisation : `Docker`, `Docker Compose`
- Orchestration : `Kubernetes`
- CI/CD : `GitHub Actions` + `GHCR`
- Monitoring : `Prometheus` + `Grafana`
- IaC : `Terraform` sur `AWS`

## Demarrage local

### 1. Base de donnees et monitoring

Le projet fournit un `docker-compose.yml` avec :

- `postgres`
- `prometheus`
- `grafana`

Commande :

```bash
docker compose up -d
```

Configuration actuelle PostgreSQL :

- base : `educhallenge`
- utilisateur : `postgres`
- mot de passe : `1234`
- port hote : `5433`

Acces monitoring :

- Prometheus : `http://localhost:9090`
- Grafana : `http://localhost:3000`
- identifiants Grafana par defaut : `admin / admin`

### 2. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Configuration backend par defaut :

- URL BDD : `jdbc:postgresql://localhost:5433/educhallenge`
- port : `8080`

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Acces frontend local :

- application : `http://localhost:5173`

## Kubernetes

Les manifests Kubernetes sont dans `k8s/` :

- `namespace.yaml`
- `postgres.yaml`
- `postgres-pvc.yaml`
- `backend-configmap.yaml`
- `backend.yaml`
- `frontend.yaml`
- `ingress.yaml`

### Deploiement manuel

```powershell
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/backend-configmap.yaml
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/ingress.yaml
```

### Preparation rapide pour la demo

Le script [scripts/prepare-k8s-demo.ps1](scripts/prepare-k8s-demo.ps1) reprepare l'environnement Kubernetes pour une demonstration locale. Il :

- relance PostgreSQL via Docker Compose
- reapplique le namespace, les secrets et les manifests
- pointe le backend Kubernetes vers la base Docker Compose deja remplie
- met a jour les images frontend/backend
- redemarre le backend et attend le rollout

Commande :

```powershell
.\scripts\prepare-k8s-demo.ps1
```

Derniere etape pour ouvrir l'application :

```powershell
kubectl -n educhallenge port-forward service/frontend-service 5173:80
```

Puis ouvrir :

```text
http://localhost:5173
```

## Monitoring

Le dossier `monitoring/` contient :

- la configuration `Prometheus`
- le provisioning `Grafana`
- les dashboards JSON

Le backend expose aussi les metriques Spring Boot / Prometheus via :

```text
/actuator/prometheus
```

## CI/CD

Le workflow GitHub Actions est dans [.github/workflows/ci-cd.yml](.github/workflows/ci-cd.yml).

Pipeline actuel :

1. tests backend Maven
2. build frontend
3. build et push des images Docker vers `GHCR`
4. deploiement Kubernetes sur runner `self-hosted`

Les images publiees sont :

- `ghcr.io/<owner>/educhallenge-backend:latest`
- `ghcr.io/<owner>/educhallenge-frontend:latest`

## Infrastructure as Code

Une couche Terraform simple basee sur AWS est disponible dans `terraform/`.

Cette stack cree :

- un `VPC`
- deux `subnets` publics
- une `Internet Gateway`
- une `EC2`
- un `security group`
- un role IAM + instance profile
- deux repositories `ECR`

Reference :

- [terraform/README.md](terraform/README.md)

## Objectif produit

EduChallenge vise a construire un ecosysteme ou apprendre devient social, mesurable et engageant. La plateforme cherche moins a distribuer du contenu passif qu'a encourager l'action : creer, participer, reussir et recommencer.
