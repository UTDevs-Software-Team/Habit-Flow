<#
.SYNOPSIS
  Servidor web local ligero para HabitFlow usando PowerShell y System.Net.HttpListener.
  No requiere Node.js, Python ni instalaciones externas.

.DESCRIPTION
  Ejecuta un servidor HTTP local en el puerto 8080 para servir los archivos de HabitFlow.
  Abre automáticamente la aplicación en tu navegador predeterminado.
#>

$port = 8080
$path = $PSScriptRoot

if (-not $path) {
    $path = Get-Location
}

$listener = New-Object System.Net.HttpListener
$url = "http://localhost:$port/"
$listener.Prefixes.Add($url)

try {
    $listener.Start()
    Write-Host "==========================================================" -ForegroundColor Cyan
    Write-Host "  HABITFLOW - Servidor Web Local Iniciado" -ForegroundColor Green
    Write-Host "==========================================================" -ForegroundColor Cyan
    Write-Host "  URL: $url" -ForegroundColor Yellow
    Write-Host "  Directorio: $path" -ForegroundColor Gray
    Write-Host "  Presiona Ctrl+C en esta terminal para detener el servidor." -ForegroundColor Gray
    Write-Host "==========================================================" -ForegroundColor Cyan

    # Intentar abrir navegador automáticamente
    try { Start-Process $url -ErrorAction SilentlyContinue } catch {}

    while ($listener.IsListening) {
        $context = $listener.GetContext()
        $request = $context.Request
        $response = $context.Response

        $localPath = $request.Url.LocalPath.TrimStart('/')
        if ([string]::IsNullOrEmpty($localPath)) {
            $localPath = "index.html"
        }

        $filePath = Join-Path $path $localPath

        if (Test-Path $filePath -PathType Leaf) {
            $bytes = [System.IO.File]::ReadAllBytes($filePath)
            
            # Detectar Content-Type
            $ext = [System.IO.Path]::GetExtension($filePath).ToLower()
            $contentType = switch ($ext) {
                ".html" { "text/html; charset=utf-8" }
                ".htm"  { "text/html; charset=utf-8" }
                ".css"  { "text/css; charset=utf-8" }
                ".js"   { "application/javascript; charset=utf-8" }
                ".json" { "application/json; charset=utf-8" }
                ".png"  { "image/png" }
                ".jpg"  { "image/jpeg" }
                ".jpeg" { "image/jpeg" }
                ".svg"  { "image/svg+xml" }
                ".ico"  { "image/x-icon" }
                default { "application/octet-stream" }
            }

            $response.ContentType = $contentType
            $response.ContentLength64 = $bytes.Length
            $response.OutputStream.Write($bytes, 0, $bytes.Length)
        } else {
            $response.StatusCode = 404
            $notFoundBytes = [System.Text.Encoding]::UTF8.GetBytes("404 - Archivo no encontrado")
            $response.ContentLength64 = $notFoundBytes.Length
            $response.OutputStream.Write($notFoundBytes, 0, $notFoundBytes.Length)
        }

        $response.Close()
    }
}
catch {
    Write-Host "Error en el servidor: $_" -ForegroundColor Red
}
finally {
    if ($listener.IsListening) {
        $listener.Stop()
    }
    $listener.Close()
    Write-Host "Servidor detenido." -ForegroundColor Gray
}
