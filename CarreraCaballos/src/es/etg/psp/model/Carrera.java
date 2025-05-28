package es.etg.psp.model;

import java.util.Random;

public class Carrera {
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador turno;
    private Random random;
    private static final int PUNTOS_OBJETIVO = 100;

    public Carrera(Jugador jugador1, Jugador jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.random = new Random();
        this.turno = random.nextBoolean() ? jugador1 : jugador2;
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public Jugador getTurno() {
        return turno;
    }

    public void cambiarTurno() {
        if (random.nextBoolean()) {
            turno = (turno == jugador1) ? jugador2 : jugador1;
        }
    }

    public int getPuntosRonda() {
        return random.nextInt(10) + 1;
    }

    public boolean hayOperacion() {
        return random.nextBoolean();
    }

    public Operacion generarOperacion() {
        int operador = random.nextInt(4);
        int num1 = random.nextInt(10) + 1;
        int num2 = random.nextInt(10) + 1;

        switch (operador) {
            case 0:
                return new Operacion(num1, num2, '+');
            case 1:
                return new Operacion(num1, num2, '-');
            case 2:
                return new Operacion(num1, num2, '*');
            case 3:
                num2 = (num2 == 0) ? 1 : num2;
                num1 = num2 * (random.nextInt(10) + 1);
                return new Operacion(num1, num2, '/');
            default:
                return null;
        }
    }

    public boolean hayGanador() {
        return jugador1.haLlegadoAPuntos(PUNTOS_OBJETIVO) || jugador2.haLlegadoAPuntos(PUNTOS_OBJETIVO);
    }

    public Jugador getGanador() {
        if (jugador1.haLlegadoAPuntos(PUNTOS_OBJETIVO))
            return jugador1;
        if (jugador2.haLlegadoAPuntos(PUNTOS_OBJETIVO))
            return jugador2;
        return null;
    }
}
