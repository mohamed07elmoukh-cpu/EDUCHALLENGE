param(
    [string]$Namespace = "educhallenge",
    [string]$GithubOwner = "mohamed07elmoukh-cpu",
    [string]$BackendTag = "latest",
    [string]$FrontendTag = "latest",
    [string]$PostgresDb = "educhallenge",
    [string]$PostgresUser = "postgres",
    [string]$PostgresPassword = "1234",
    [string]$ExternalDbHost = "host.docker.internal",
    [int]$ExternalDbPort = 5433,
    [switch]$SkipDockerCompose
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$k8sDir = Join-Path $repoRoot "k8s"
$backendImage = "ghcr.io/$GithubOwner/educhallenge-backend:$BackendTag"
$frontendImage = "ghcr.io/$GithubOwner/educhallenge-frontend:$FrontendTag"
$jdbcUrl = "jdbc:postgresql://$ExternalDbHost`:$ExternalDbPort/$PostgresDb"

function Assert-Command {
    param([string]$Name)

    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "Required command not found: $Name"
    }
}

function Write-Step {
    param([string]$Message)
    Write-Host "==> $Message" -ForegroundColor Cyan
}

Assert-Command kubectl
Assert-Command docker

Write-Step "Using repository root $repoRoot"
Write-Step "Kubernetes context: $(kubectl config current-context)"

if (-not $SkipDockerCompose) {
    Write-Step "Starting Docker Compose postgres"
    Push-Location $repoRoot
    try {
        docker compose up -d postgres | Out-Host
    }
    finally {
        Pop-Location
    }

    Write-Step "Waiting for local postgres container to become ready"
    $postgresReady = $false
    for ($i = 0; $i -lt 20; $i++) {
        try {
            $result = docker exec educhallenge-postgres pg_isready -U $PostgresUser -d $PostgresDb 2>$null
            if ($LASTEXITCODE -eq 0) {
                $postgresReady = $true
                break
            }
        }
        catch {
        }
        Start-Sleep -Seconds 3
    }

    if (-not $postgresReady) {
        throw "Local postgres container did not become ready. Check docker compose logs."
    }
}

Write-Step "Applying namespace"
kubectl apply -f (Join-Path $k8sDir "namespace.yaml") | Out-Host

Write-Step "Creating or updating application secret"
kubectl -n $Namespace create secret generic educhallenge-secrets `
    --from-literal=POSTGRES_DB=$PostgresDb `
    --from-literal=POSTGRES_USER=$PostgresUser `
    --from-literal=POSTGRES_PASSWORD=$PostgresPassword `
    --dry-run=client -o yaml | kubectl apply -f - | Out-Host

Write-Step "Pointing backend to Docker Compose postgres"
kubectl -n $Namespace create configmap backend-config `
    --from-literal=SPRING_DATASOURCE_URL=$jdbcUrl `
    --dry-run=client -o yaml | kubectl apply -f - | Out-Host

Write-Step "Applying backend and frontend manifests"
kubectl apply -f (Join-Path $k8sDir "backend.yaml") | Out-Host
kubectl apply -f (Join-Path $k8sDir "frontend.yaml") | Out-Host

Write-Step "Updating deployment images"
kubectl -n $Namespace set image deployment/backend backend=$backendImage | Out-Host
kubectl -n $Namespace set image deployment/frontend frontend=$frontendImage | Out-Host

$postgresDeploymentName = kubectl -n $Namespace get deployment postgres --ignore-not-found -o name
if ($postgresDeploymentName) {
    Write-Step "Scaling down in-cluster postgres so the app uses Docker Compose data"
    kubectl -n $Namespace scale deployment/postgres --replicas=0 | Out-Host
}

Write-Step "Restarting backend to reload datasource configuration"
kubectl -n $Namespace rollout restart deployment/backend | Out-Host

Write-Step "Waiting for rollout"
kubectl -n $Namespace rollout status deployment/backend --timeout=180s | Out-Host
kubectl -n $Namespace rollout status deployment/frontend --timeout=180s | Out-Host

Write-Step "Current application state"
kubectl -n $Namespace get pods,svc | Out-Host

Write-Host ""
Write-Host "Environment is ready." -ForegroundColor Green
Write-Host "Last step for the demo:" -ForegroundColor Green
Write-Host "kubectl -n $Namespace port-forward service/frontend-service 5173:80" -ForegroundColor Yellow
