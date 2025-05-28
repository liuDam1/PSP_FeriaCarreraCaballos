package test.es.etg.psp;

import org.junit.jupiter.api.Test;
import es.etg.psp.model.Jugador;

import static org.junit.jupiter.api.Assertions.*;

public class JugadorTest {

    @Test
    public void testCrearJugador() {
        Jugador jugador = new Jugador("TestPlayer");
        assertEquals("TestPlayer", jugador.getNombre());
        assertEquals(0, jugador.getPuntos());
    }

    @Test
    public void testSumarPuntos() {
        Jugador jugador = new Jugador("TestPlayer");
        jugador.sumarPuntos(10);
        assertEquals(10, jugador.getPuntos());
    }

    @Test
    public void testVerificarPuntosObjetivo() {
        Jugador jugador = new Jugador("TestPlayer");
        jugador.sumarPuntos(150);
        assertTrue(jugador.haLlegadoAPuntos(100));
    }
}
