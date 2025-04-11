# PSP_FeriaCarreraCaballos
# índice
- [Introducción](#introducción)
    - [Objetivo del juego](#objetivo-del-juego)
    - [Características principales](#características-principales)
- [Análisis y prototipo](#análisis-y-prototipo)


# Introducción
## Objetivo del juego
Dos jugadores competirán para llegar primero a la meta. Cada jugador tendrá un camello asignado y deberá registrarse con un nombre único. El juego avanzará automáticamente, moviendo los camellos hasta que uno de ellos cruce la línea de meta y gana un certificado de ganador.

## Características principales
- **Registro de jugadores:**
    - Cada jugador ingresa su nombre antes de comenzar la partida.
    - Pueden conectarse desde dispositivos diferentes ( En red local o en línea).

- **Dinámica del juego:**
    - Los camellos avanzarán automáticamente y en turno aleatoria.
    - El primer camello en llegar a la meta gana la partida.

- **Premio y certificado:**
    - El jugador ganador podrá generar e imprimir un PDF con un certificado de victoria personalizado.

- **Ventajas especiales (opcional):**
    - Los jugadores recibirán avances extra durante la carrera bajo ciertas condiciones (ejemplo: por respuestas correctas en preguntas aleatorias, por tiempo de reacción, etc.).

- **Interfaz gráfica intuitiva:**
    - Habrá una interfaz fácil de usar para que los jugadores puedan interactuar con el juego y ver el progreso de la carrera.

- **Historial de partidas:**
    - El juego llevará un registro de resultados para consulta futura.

# Análisis y prototipo

## Análisis

### Requisitos Funcionales

- **Registro de jugadores:**
    - Cada jugador ingresa su nombre antes de comenzar

- **Asignación de camellos:**
    - Cada jugador tiene un camello con atributos (color, numero.)

- **Mecánica de carrera:**
    - Movimiento automático con avances aleatorios
    - Posibilidad de "avances extra" por condiciones especiales (ej: minijuegos, respuestas rápidas)

- **Determinación del ganador:**
    - El primer camello en alcanzar la meta (ej: 100 pasos) gana

- **Generación de certificado:**
    - El ganador puede descargar un PDF con su nombre, fecha y resultado

- **Interfaz gráfica (GUI):**
    - Pantalla de inicio (registro)
    - Tablero de carrera (visualización de avances)
    - Panel de resultados (ganador, historial)

- **Persistencia de datos:**
    - Guardar historial de partidas en base de dato o fichero plano(ganadores, tiempos, etc.)

### Requisitos No Funcionales

- **Diseño de interfaz:**
    - Interfaz echo por SceneBuilder de JavaFX

- **Comunicación en red (opcional):**
    - Posibilidad de jugar en red local o en línea (con sockets)

- **Generación de PDF:**
    - Se genera en markdown y se convierte a PDF con Doker.

- **Base de datos simple:**
    - SQLite o txt para almacenar historial

## 2. Prototipo

### Diagrama de Flujo

@startuml CarreraDeCamellos
title Diagrama de Flujo - Carrera de Camellos
skinparam monochrome true
skinparam shadowing false
skinparam defaultFontName Arial
skinparam activity {
    BackgroundColor White
    BorderColor Black
    FontColor Black
}

start
:Inicio del Juego;
note right: Versión 1.0

partition "Fase de Registro" {
    :Pantalla de Registro;
    repeat
      :Esperar 2 jugadores;
      note left: Validar nombres únicos
    repeat while (¿2 jugadores registrados?) is (No)
    ->Sí;
}

partition "Carrera" {
    :Asignar camellos\n(Color/Número);
    :Iniciar Carrera;
    repeat
      :Avance aleatorio;
      if (¿Evento especial?) then (Sí)
        :Otorgar avance extra;
      else (No)
      endif
    repeat while (¿Alguien llegó a la meta?) is (No)
    ->Sí;
}

partition "Post-Juego" {
    :Mostrar Ganador;
    :Generar Markdown\n(nombre, fecha, avatar);
    :Convertir a PDF\n(vía Docker/Pandoc);
    :Guardar en Historial\n(SQLite/txt);
    :Volver a Inicio;
}

stop

@enduml
