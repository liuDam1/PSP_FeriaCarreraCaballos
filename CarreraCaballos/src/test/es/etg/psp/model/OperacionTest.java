package test.es.etg.psp.model;

import org.junit.jupiter.api.Test;
import es.etg.psp.model.Operacion;

import static org.junit.jupiter.api.Assertions.*;

public class OperacionTest {

    @Test
    public void testSuma() {
        Operacion operacion = new Operacion(5, 3, '+');
        assertEquals(8, operacion.getResultado());
    }

    @Test
    public void testResta() {
        Operacion operacion = new Operacion(5, 3, '-');
        assertEquals(2, operacion.getResultado());
    }

    @Test
    public void testMultiplicacion() {
        Operacion operacion = new Operacion(5, 3, '*');
        assertEquals(15, operacion.getResultado());
    }

    @Test
    public void testDivision() {
        Operacion operacion = new Operacion(6, 3, '/');
        assertEquals(2, operacion.getResultado());
    }

    @Test
    public void testVerificarResultadoCorrecto() {
        Operacion operacion = new Operacion(6, 3, '/');
        assertTrue(operacion.verificarResultado(2));
    }

    @Test
    public void testVerificarResultadoIncorrecto() {
        Operacion operacion = new Operacion(6, 3, '/');
        assertFalse(operacion.verificarResultado(3));
    }
}
