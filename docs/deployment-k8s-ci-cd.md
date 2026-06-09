# Deploiement Kubernetes et pipeline CI/CD

## Architecture cible

- `frontend` : build Vite, servi par `nginx`
- `backend` : application Spring Boot sur le port `8080`
- `postgres` : base de donnees dans le cluster avec volume persistant
- `ingress` : point d'entree HTTP

Le frontend expose l'UI sur `/` et reverse-proxy automatiquement `/api/*` vers le service Kubernetes `backend-service:8080`.

## Fichiers ajoutes

- `backend/Dockerfile`
- `frontend/Dockerfile`
- `frontend/nginx.conf`
- `k8s/*.yaml`
- `.github/workflows/ci-cd.yml`

## Prerequis

- un cluster Kubernetes
- un Ingress Controller `nginx`
- un registre d'images accessible par le cluster
- GitHub Actions active sur le depot

## Secrets GitHub a configurer

Dans `Settings > Secrets and variables > Actions`, ajouter :

- `KUBE_CONFIG_B64` : kubeconfig encode en base64
- `POSTGRES_DB` : `educhallenge`
- `POSTGRES_USER` : utilisateur PostgreSQL
- `POSTGRES_PASSWORD` : mot de passe PostgreSQL

Commande pour generer `KUBE_CONFIG_B64` :

```bash
base64 -w 0 ~/.kube/config
```

Sous PowerShell :

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$HOME\.kube\config"))
```

## Etapes de deploiement manuel

1. Builder et publier les images :

```bash
docker build -t ghcr.io/<owner>/educhallenge-backend:latest ./backend
docker build -t ghcr.io/<owner>/educhallenge-frontend:latest --build-arg VITE_API_BASE_URL=/api ./frontend
docker push ghcr.io/<owner>/educhallenge-backend:latest
docker push ghcr.io/<owner>/educhallenge-frontend:latest
```

2. Creer le namespace et le secret :

```bash
kubectl apply -f k8s/namespace.yaml
kubectl -n educhallenge create secret generic educhallenge-secrets \
  --from-literal=POSTGRES_DB=educhallenge \
  --from-literal=POSTGRES_USER=postgres \
  --from-literal=POSTGRES_PASSWORD=1234
```

3. Appliquer les manifests :

```bash
kubectl apply -f k8s/backend-configmap.yaml
kubectl apply -f k8s/postgres-pvc.yaml
kubectl apply -f k8s/postgres.yaml
kubectl apply -f k8s/backend.yaml
kubectl apply -f k8s/frontend.yaml
kubectl apply -f k8s/ingress.yaml
```

4. Remplacer les images placeholder si necessaire :

```bash
kubectl -n educhallenge set image deployment/backend backend=ghcr.io/<owner>/educhallenge-backend:latest
kubectl -n educhallenge set image deployment/frontend frontend=ghcr.io/<owner>/educhallenge-frontend:latest
```

5. Verifier le rollout :

```bash
kubectl -n educhallenge get pods
kubectl -n educhallenge rollout status deployment/postgres
kubectl -n educhallenge rollout status deployment/backend
kubectl -n educhallenge rollout status deployment/frontend
```

## Fonctionnement du pipeline

Sur `pull_request` vers `main` :

- tests backend Maven
- build frontend Vite

Sur `push` vers `main` :

- tests backend
- build frontend
- build et push des images dans `ghcr.io`
- deploiement automatique sur Kubernetes

## Points d'attention

- Les images `ghcr.io` doivent etre publiques, ou bien le cluster doit disposer d'un `imagePullSecret`.
- Le manifest `k8s/backend.yaml` contient une image placeholder ; le pipeline la remplace avec l'image du commit courant.
- `postgres` dans Kubernetes convient pour dev, demo ou petit environnement. En production, preferer une base geree.

## Restauration rapide pour la demo locale

Sur `docker-desktop`, le cluster Kubernetes local peut etre recree ou perdre son etat. Pour remettre rapidement l'application en etat de demo sans retaper toutes les commandes, utiliser :

```powershell
.\scripts\prepare-k8s-demo.ps1
```

Ce script :

- demarre `postgres` via `docker compose`
- recree le namespace et les secrets
- reconfigure le backend K8s pour utiliser la base PostgreSQL Docker Compose deja remplie
- reapplique les manifests `backend` et `frontend`
- remet les images `ghcr.io`
- attend que les pods applicatifs soient `Running`

Une fois le script termine, il ne reste qu'a lancer :

```powershell
kubectl -n educhallenge port-forward service/frontend-service 5173:80
```
