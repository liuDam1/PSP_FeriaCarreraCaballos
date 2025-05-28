package test.es.etg.psp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import es.etg.psp.model.Carrera;
import es.etg.psp.model.Jugador;
import es.etg.psp.model.Operacion;

import static org.junit.jupiter.api.Assertions.*;

public class CarreraTest {

    private Jugador jugador1;
    private Jugador jugador2;
    private Carrera carrera;

    @BeforeEach
    public void setUp() {
        jugador1 = new Jugador("Player1");
        jugador2 = new Jugador("Player2");
        carrera = new Carrera(jugador1, jugador2);
    }

    @Test
    public void testInicializacionCarrera() {
        assertNotNull(carrera.getJugador1());
        assertNotNull(carrera.getJugador2());
        assertNotNull(carrera.getTurno());
    }

    @Test
    public void testCambioDeTurno() {
        Jugador turnoInicial = carrera.getTurno();
        carrera.cambiarTurno();
        assertNotEquals(turnoInicial, carrera.getTurno());
    }

    @Test
    public void testVerificarGanador() {
        jugador1.sumarPuntos(100);
        assertTrue(carrera.hayGanador());
    }

    @Test
    public void testGenerarOperacion() {
        Operacion operacion = carrera.generarOperacion();
        assertNotNull(operacion);
    }
}
