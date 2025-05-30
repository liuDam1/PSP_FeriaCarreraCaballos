# PSP_FeriaCarreraCaballos
# índice
- [Introducción](#introducción)
    - [Objetivo del juego](#objetivo-del-juego)
    - [Características principales](#características-principales)
- [Análisis y prototipo](#análisis-y-prototipo)
    - [Análisis](#análisis)
        - [Requisitos Funcionales](#requisitos-funcionales)
        - [Requisitos No Funcionales](#requisitos-no-funcionales)
    - [Prototipo](#prototipo)
        - [Diagrama de Flujo](#diagrama-de-flujo)
- [Arquitectura, Diseño y Plan de Pruebas](#arquitectura-diseño-y-plan-de-pruebas)
    - [Arqitectura](#arquitectura)
    - [Diseño](#diseño)
        - [Diagrama de Despliegue](#diagrama-de-despliegue)
        - [Diagrama de Clases](#diagrama-de-clases)
    - [Plan de Pruebas](#plan-de-pruebas)
        
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

- **Rendimiento**
    - Interfaz responde en <1s a acciones del usuario
    - Sincronización en red con latencia máxima de 500ms
    - Generación de PDF en ≤5 segundos

- **Escalabilidad**
    - Base de datos soporta 10,000 registros de historial
    - Arquitectura permite 2 jugadores (modo red)

- **Usabilidad**
    - Interfaz JavaFX con SceneBuilder
    - Tamaño de fuente adaptable.

- **Portabilidad**
    - Compatible con:
        - Windows 10+
        - Generación de PDF multiplataforma mediante Docker.

- **Fiabilidad**
    - Persistencia de datos garantizada (SQLite o TXT).
    - Recuperación de datos en fallos

- **Mantenibilidad**
    - Código modular (MVC)

- **Seguridad**
    - Validación de:
        - Nombres únicos (sin caracteres especiales)
        - IPs válidas en conexión red

## Prototipo

### Diagrama de Flujo

```mermaid
graph TD
    A[Inicio] --> B[Registro]
    B --> C[Esperar 2 jugadores]
    C --> D[Iniciar Carrera]
    D --> E[Avance Aleatorio de Camellos]
    E --> F{¿Alguien llegó a la meta?}
    F -- Sí --> G[Mostrar Ganador]
    G --> H[Generar Markdown y transformar a PDF]
    F -- No --> E
    H --> I[Guardar en Historial]
    I --> J[Volver a Inicio]
```

# Arquitectura, Diseño y Plan de Pruebas
## Arquitectura 
- Cliente servidor.
- Lenguaje de programación Java.
- Patrón MVC.
- Uso de JavaFX para la interfaz gráfica.
- Uso de SQLite o txt para la persistencia de datos.
- Uso de Docker para la generación de markdown a PDF.
- Uso de sockets para la comunicación en red (si se implementa).
- Uso de SceneBuilder para la creación de la interfaz gráfica.

## Diseño
### GUI
<img src="./Multimedia/GUI_Inicio.png">

<img src="./Multimedia/GUI_Juego.png">

### Diagrama de Despliegue
<img src="./Multimedia/DiagramaDespliegue.png">


## Plan de pruebas
- Registrar jugador y asignar caballos.
- Los cavallos avanzan dependiendo del numero generado.
- Muestra el ganador.
- Turnos y avances son aleatorios.
- Genera el certificado.
- Guarda el historial.
- Conexión desde otro equipo al servidor.
