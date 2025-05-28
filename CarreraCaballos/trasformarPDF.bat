@echo off
cd /d "%~dp0"

docker run --rm -v %cd%:/data pandoc/latex certificado_ganador.md -o certificado_ganador.pdf

if %errorlevel% neq 0 (
    echo Error al convertir Markdown a PDF
    exit /b 1
) else (
    echo PDF generado correctamente.
)
