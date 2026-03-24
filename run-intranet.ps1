Write-Host 'Deteniendo stack anterior...' -ForegroundColor Cyan
docker compose down -v
if ($LASTEXITCODE -ne 0) { Write-Host 'ERROR down'; exit 1 }

Write-Host 'Construyendo imágenes...' -ForegroundColor Cyan
docker compose build --no-cache
if ($LASTEXITCODE -ne 0) { Write-Host 'ERROR build'; exit 1 }

Write-Host 'Iniciando contenedores...' -ForegroundColor Cyan
docker compose up -d
if ($LASTEXITCODE -ne 0) { Write-Host 'ERROR up'; exit 1 }

Start-Sleep -Seconds 5
docker compose ps

Write-Host 'Abriendo http://localhost:4200' -ForegroundColor Cyan
Start-Process 'http://localhost:4200'