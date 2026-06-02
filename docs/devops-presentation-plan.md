# Plan DevOps pret a presenter

## 1. Objectif de la partie DevOps

La partie DevOps du projet `EduChallenge` sert a automatiser et fiabiliser le cycle de livraison :

- valider le code a chaque changement
- construire les artefacts applicatifs
- publier les images Docker
- deployer automatiquement l'application sur Kubernetes
- verifier que le deploiement fonctionne

Le but est de passer d'un developpement manuel a une chaine `CI/CD` reproductible.

## 2. Architecture technique resumee

Le projet est compose de trois briques principales :

- `frontend` : application `React + Vite`
- `backend` : API `Spring Boot`
- `postgres` : base de donnees `PostgreSQL`

En environnement Kubernetes :

- le frontend est servi par `nginx`
- le backend tourne dans un `Deployment`
- PostgreSQL tourne avec un volume persistant
- un `Ingress` expose l'application

## 3. Role des fichiers DevOps

### Workflow GitHub Actions

- [.github/workflows/ci-cd.yml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/.github/workflows/ci-cd.yml:1)
  Ce fichier definit la pipeline CI/CD.
  Il lance :
  - les tests backend
  - le build frontend
  - le build et le push des images Docker
  - le deploiement sur Kubernetes

### Docker backend

- [backend/Dockerfile](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/backend/Dockerfile:1)
  Construit l'image du backend Spring Boot en deux etapes :
  - build Maven
  - image runtime Java legere

- [backend/.dockerignore](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/backend/.dockerignore:1)
  Empeche d'envoyer au build Docker les fichiers inutiles comme `target`, logs et fichiers temporaires.

### Docker frontend

- [frontend/Dockerfile](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/frontend/Dockerfile:1)
  Construit le frontend Vite, puis le sert avec `nginx`.

- [frontend/nginx.conf](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/frontend/nginx.conf:1)
  Sert les fichiers statiques et redirige `/api` vers le backend Kubernetes.

- [frontend/.dockerignore](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/frontend/.dockerignore:1)
  Evite d'inclure `node_modules` et `dist` dans le contexte Docker.

### Configuration frontend

- [frontend/src/services/api.js](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/frontend/src/services/api.js:1)
  Definit l'URL API par defaut sur `/api`, ce qui simplifie l'integration avec l'Ingress et `nginx`.

- [frontend/vite.config.js](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/frontend/vite.config.js:1)
  Active un proxy local en developpement pour envoyer `/api` vers `http://localhost:8080`.

### Kubernetes

- [k8s/namespace.yaml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/k8s/namespace.yaml:1)
  Cree le namespace `educhallenge`.

- [k8s/backend-configmap.yaml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/k8s/backend-configmap.yaml:1)
  Contient la configuration non sensible du backend, ici l'URL JDBC.

- [k8s/postgres-pvc.yaml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/k8s/postgres-pvc.yaml:1)
  Reserve le stockage persistant pour PostgreSQL.

- [k8s/postgres.yaml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/k8s/postgres.yaml:1)
  Deploie PostgreSQL et son `Service`.

- [k8s/backend.yaml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/k8s/backend.yaml:1)
  Deploie l'API backend et son `Service`.

- [k8s/frontend.yaml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/k8s/frontend.yaml:1)
  Deploie l'application frontend et son `Service`.

- [k8s/ingress.yaml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/k8s/ingress.yaml:1)
  Expose l'application au trafic HTTP via un controleur Ingress.

### Documentation

- [docs/deployment-k8s-ci-cd.md](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/docs/deployment-k8s-ci-cd.md:1)
  Decrit les prerequis, secrets, commandes manuelles et le comportement du pipeline.

## 4. Secrets et variables a expliquer

La chaine CI/CD depend de secrets GitHub :

- `KUBE_CONFIG_B64` : acces au cluster Kubernetes
- `POSTGRES_DB` : nom de la base
- `POSTGRES_USER` : utilisateur PostgreSQL
- `POSTGRES_PASSWORD` : mot de passe PostgreSQL

A presenter clairement :

- les `ConfigMap` stockent les variables non sensibles
- les `Secrets` stockent les donnees sensibles
- GitHub Actions injecte ces valeurs au moment du deploiement

## 5. Flux complet commit -> build -> push -> deploy

### Etape 1 : commit et push

Le developpeur modifie le code puis pousse sur GitHub.

Exemple :

```bash
git add .
git commit -m "Add feature and update deployment"
git push origin main
```

### Etape 2 : declenchement du workflow

Le fichier `ci-cd.yml` se declenche :

- sur `pull_request` vers `main`
- sur `push` vers `main`

### Etape 3 : phase CI

La pipeline verifie que l'application est correcte avant livraison :

- `backend-test`
  - installe Java 21
  - execute `./mvnw test`

- `frontend-build`
  - installe Node.js 22
  - execute `npm ci`
  - execute `npm run build`

Resultat :

- si un test echoue, le processus s'arrete
- si le build frontend echoue, le processus s'arrete

### Etape 4 : build et push des images

Si le `push` est fait sur `main`, le job `docker-images` :

- se connecte au registre `ghcr.io`
- build l'image backend
- push l'image backend
- build l'image frontend
- push l'image frontend

Les images sont taguees avec :

- le `commit SHA`
- `latest`

Interet :

- `SHA` permet la tracabilite exacte
- `latest` facilite les deploiements simples

### Etape 5 : deploiement Kubernetes

Le job `deploy` :

- configure `kubectl`
- charge le `kubeconfig`
- cree ou met a jour les secrets Kubernetes
- applique les manifests
- remplace l'image du backend par celle du commit
- remplace l'image du frontend par celle du commit
- attend la fin du rollout

### Etape 6 : application disponible

Une fois le rollout termine :

- le frontend repond via l'Ingress
- le frontend appelle `/api`
- `nginx` ou l'Ingress redirige vers le backend
- le backend communique avec PostgreSQL

## 6. Version courte a dire a l'oral

Phrase simple :

> A chaque push sur `main`, GitHub Actions teste le backend, build le frontend, construit les images Docker, les publie dans `ghcr.io`, puis deploie automatiquement l'application sur Kubernetes avec verification du rollout.

## 7. Demonstration en reunion ou devant un prof

### Demo 1 : montrer la pipeline

Objectif :

- prouver que le projet a une vraie chaine CI/CD

A montrer :

- le fichier [ci-cd.yml](C:/Users/moham/Desktop/Educhallenge/development-platform-alucaard/.github/workflows/ci-cd.yml:1)
- l'onglet `Actions` sur GitHub
- les jobs `backend-test`, `frontend-build`, `docker-images`, `deploy`

Ce qu'il faut dire :

- `pull_request` = validation
- `push main` = validation + livraison + deploiement

### Demo 2 : montrer les images Docker

Commandes :

```bash
docker build -t educhallenge-backend:demo ./backend
docker build -t educhallenge-frontend:demo --build-arg VITE_API_BASE_URL=/api ./frontend
docker images | grep educhallenge
```

Sous PowerShell :

```powershell
docker build -t educhallenge-backend:demo .\backend
docker build -t educhallenge-frontend:demo --build-arg VITE_API_BASE_URL=/api .\frontend
docker images
```

Ce qu'il faut dire :

- chaque service a sa propre image
- les images sont reproductibles et pretes pour le deploiement

### Demo 3 : montrer la partie Kubernetes

Commandes :

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/backend-configmap.yaml
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/ingress.yaml
```

Puis :

```bash
kubectl get ns
kubectl get all -n educhallenge
kubectl get ingress -n educhallenge
kubectl get pvc -n educhallenge
```

Ce qu'il faut dire :

- le namespace isole le projet
- les deployments gerent les pods
- les services exposent les applications en interne
- l'ingress gere l'entree HTTP
- le PVC conserve les donnees PostgreSQL

### Demo 4 : montrer les logs et la sante

Commandes :

```bash
kubectl -n educhallenge rollout status deployment/postgres
kubectl -n educhallenge rollout status deployment/backend
kubectl -n educhallenge rollout status deployment/frontend
kubectl -n educhallenge logs deployment/backend
kubectl -n educhallenge logs deployment/frontend
```

Ce qu'il faut dire :

- le rollout confirme que le deploiement est termine
- les logs servent au diagnostic

### Demo 5 : montrer le cycle complet

Scenario conseille :

1. modifier une petite valeur dans le frontend ou le backend
2. faire un commit
3. faire un push
4. ouvrir GitHub Actions
5. montrer le lancement du workflow
6. montrer ensuite `kubectl get pods -n educhallenge`

Commande exemple :

```bash
git add .
git commit -m "Demo CI/CD update"
git push origin main
```

Message a expliquer :

- un simple push suffit pour relivrer l'application
- la livraison est industrialisee

## 8. Commandes de demonstration pretes a copier

### Verification locale backend

```bash
cd backend
./mvnw test
```

Sous PowerShell :

```powershell
cd .\backend
.\mvnw.cmd test
```

### Verification locale frontend

```bash
cd frontend
npm ci
npm run build
```

Sous PowerShell :

```powershell
cd .\frontend
npm ci
npm run build
```

### Construction Docker

```bash
docker build -t ghcr.io/<owner>/educhallenge-backend:latest ./backend
docker build -t ghcr.io/<owner>/educhallenge-frontend:latest --build-arg VITE_API_BASE_URL=/api ./frontend
```

### Deploiement Kubernetes

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/backend-configmap.yaml
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/ingress.yaml
```

### Verification cluster

```bash
kubectl get pods -n educhallenge
kubectl get svc -n educhallenge
kubectl get ingress -n educhallenge
kubectl get pvc -n educhallenge
```

### Diagnostic

```bash
kubectl -n educhallenge describe deployment backend
kubectl -n educhallenge describe deployment frontend
kubectl -n educhallenge logs deployment/backend
kubectl -n educhallenge logs deployment/frontend
```

## 9. Points forts a mettre en avant

- automatisation complete du pipeline
- separation claire entre application et infrastructure
- conteneurisation independante du frontend et du backend
- deploiement reproductible
- base de donnees persistante
- configuration externalisee
- verification du rollout apres deploiement

## 10. Limites actuelles a dire honnetement

- PostgreSQL tourne dans le cluster : acceptable pour demo ou petit environnement, moins adapte a une vraie production
- pas encore de monitoring avance type `Prometheus/Grafana`
- pas encore de strategie `staging` / `production`
- pas encore de `Helm chart` ou `Kustomize`
- pas encore de scan securite d'images ou de dependances

## 11. Ameliorations possibles

- ajouter un environnement `staging`
- ajouter des tests d'integration
- ajouter un `imagePullSecret` si les images sont privees
- ajouter `Prometheus` et `Grafana`
- remplacer PostgreSQL interne par une base managee
- ajouter `Helm` pour versionner les deploiements

## 12. Conclusion prete a prononcer

Conclusion possible :

> La partie DevOps de ce projet permet de transformer un simple depot applicatif en plateforme livrable automatiquement. Grace a GitHub Actions, Docker et Kubernetes, chaque changement peut etre teste, package, publie et deploye de maniere standardisee et reproductible.
