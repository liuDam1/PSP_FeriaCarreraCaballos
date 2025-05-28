package es.etg.psp.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import es.etg.psp.model.Operacion;

public class Cliente {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final String DIRECCION_SERVIDOR = "localhost";
    private static final int PUERTO = 44444;
    
    // Constantes para mensajes de conexión
    private static final String MENSAJE_CONECTANDO = "Cliente: Intentando conectar a ";
    private static final String MENSAJE_CONEXION_EXITOSA = "Cliente: Conexión establecida con éxito";
    private static final String MENSAJE_FLUJOS_INICIALIZADOS = "Cliente: Flujos de entrada/salida inicializados";
    private static final String MENSAJE_ERROR_CONEXION = "Cliente: Error al conectar: ";
    
    // Constantes para mensajes de jugadores
    private static final String MENSAJE_ENVIANDO_JUGADORES = "Cliente: Enviando jugadores - ";
    private static final String MENSAJE_JUGADORES_ENVIADOS = "Cliente: Jugadores enviados correctamente";
    private static final String MENSAJE_ERROR_ENVIAR_JUGADORES = "Cliente: Error al enviar jugadores: ";
    
    // Constantes para operaciones
    private static final String SOLICITUD_OPERACION = "SOLICITAR_OPERACION";
    private static final String MENSAJE_SOLICITANDO_OPERACION = "Cliente: Solicitando operación";
    private static final String MENSAJE_OPERACION_RECIBIDA = "Cliente: Operación recibida - ";
    private static final String MENSAJE_ERROR_SOLICITAR_OPERACION = "Cliente: Error al solicitar operación: ";
    
    // Constantes para verificación de respuesta
    private static final String SOLICITUD_VERIFICACION = "VERIFICAR_RESPUESTA";
    private static final String MENSAJE_ENVIANDO_RESPUESTA = "Cliente: Enviando respuesta - ";
    private static final String MENSAJE_RESPUESTA_CORRECTA = "Cliente: Respuesta correcta";
    private static final String MENSAJE_ERROR_VERIFICACION = "Cliente: Error al verificar respuesta: ";
    
    // Constantes para cierre de conexión
    private static final String MENSAJE_CIERRE_CONEXION = "Cliente: Cerrando conexión";

    public Cliente() throws IOException {
        try {
            System.out.println(MENSAJE_CONECTANDO + DIRECCION_SERVIDOR + ":" + PUERTO);
            socket = new Socket(DIRECCION_SERVIDOR, PUERTO);
            System.out.println(MENSAJE_CONEXION_EXITOSA);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println(MENSAJE_FLUJOS_INICIALIZADOS);
        } catch (IOException e) {
            System.err.println(MENSAJE_ERROR_CONEXION + e.getMessage());
            throw e;
        }
    }

    public void enviarJugadores(String jugador1, String jugador2) throws IOException {
        try {
            System.out.println(MENSAJE_ENVIANDO_JUGADORES + jugador1 + " y " + jugador2);
            out.writeObject(jugador1);
            out.writeObject(jugador2);
            out.flush();
            System.out.println(MENSAJE_JUGADORES_ENVIADOS);
        } catch (IOException e) {
            System.err.println(MENSAJE_ERROR_ENVIAR_JUGADORES + e.getMessage());
            cerrarConexion();
            throw e;
        }
    }

    public Operacion solicitarOperacion() throws IOException, ClassNotFoundException {
        try {
            System.out.println(MENSAJE_SOLICITANDO_OPERACION);
            out.writeObject(SOLICITUD_OPERACION);
            out.flush();

            Operacion operacion = (Operacion) in.readObject();
            System.out.println(MENSAJE_OPERACION_RECIBIDA +
                    operacion.getNum1() + " " +
                    operacion.getOperador() + " " +
                    operacion.getNum2());
            return operacion;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(MENSAJE_ERROR_SOLICITAR_OPERACION + e.getMessage());
            cerrarConexion();
            throw e;
        }
    }

    public boolean verificarRespuesta(int respuesta, Operacion operacion) throws IOException, ClassNotFoundException {
        try {
            System.out.println(MENSAJE_ENVIANDO_RESPUESTA + respuesta);
            out.writeObject(SOLICITUD_VERIFICACION);
            out.writeObject(respuesta);
            out.flush();

            boolean esCorrecta = (Boolean) in.readObject();
            System.out.println(MENSAJE_RESPUESTA_CORRECTA + (esCorrecta ? "correcta" : "incorrecta"));
            return esCorrecta;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(MENSAJE_ERROR_VERIFICACION + e.getMessage());
            cerrarConexion();
            throw e;
        }
    }

    public void cerrarConexion() {
        System.out.println(MENSAJE_CIERRE_CONEXION);
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
