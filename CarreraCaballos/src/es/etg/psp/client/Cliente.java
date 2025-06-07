package es.etg.psp.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import es.etg.psp.model.Operacion;
import es.etg.psp.util.TipoLog;
import es.etg.psp.util.GestionLog;

public class Cliente {
    // Constantes de conexión
    private static final String DIRECCION_SERVIDOR = "localhost";
    private static final int PUERTO = 44444;
    
    // Constantes para comandos del servidor
    private static final String SOLICITUD_OPERACION = "SOLICITAR_OPERACION";
    private static final String VERIFICACION_RESPUESTA = "VERIFICAR_RESPUESTA";
    
    // Constantes para mensajes de log
    private static final String LOG_INTENTO_CONEXION = "Intentando conectar al servidor en %s:%d";
    private static final String LOG_CONEXION_EXITOSA = "Conexión establecida con éxito";
    private static final String LOG_FLUJOS_INICIALIZADOS = "Flujos de entrada/salida inicializados";
    private static final String LOG_ERROR_CONEXION = "Error al conectar: %s";
    private static final String LOG_ENVIO_JUGADORES = "Enviando jugadores: %s y %s";
    private static final String LOG_JUGADORES_ENVIADOS = "Jugadores enviados correctamente";
    private static final String LOG_ERROR_ENVIO_JUGADORES = "Error al enviar jugadores: %s";
    private static final String LOG_SOLICITUD_OPERACION = "Solicitando operación al servidor";
    private static final String LOG_OPERACION_RECIBIDA = "Operación recibida: %d %c %d";
    private static final String LOG_ERROR_SOLICITUD_OPERACION = "Error al solicitar operación: %s";
    private static final String LOG_ENVIO_RESPUESTA = "Enviando respuesta para verificación: %d";
    private static final String LOG_RESPUESTA_VERIFICADA = "Respuesta verificada como: %s";
    private static final String LOG_ERROR_VERIFICACION = "Error al verificar respuesta: %s";
    private static final String LOG_CIERRE_CONEXION = "Cerrando conexión con el servidor";
    private static final String LOG_CIERRE_FLUJO_ENTRADA = "Flujo de entrada cerrado";
    private static final String LOG_CIERRE_FLUJO_SALIDA = "Flujo de salida cerrado";
    private static final String LOG_CIERRE_SOCKET = "Socket cerrado";
    private static final String LOG_ERROR_CIERRE = "Error al cerrar conexión: %s";
    
    // Textos para verificación
    private static final String TEXTO_CORRECTO = "CORRECTA";
    private static final String TEXTO_INCORRECTO = "INCORRECTA";
    
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    public Cliente() throws IOException {
        try {
            GestionLog.registrar(TipoLog.INFO, String.format(LOG_INTENTO_CONEXION, DIRECCION_SERVIDOR, PUERTO));
            socket = new Socket(DIRECCION_SERVIDOR, PUERTO);
            GestionLog.registrar(TipoLog.INFO, LOG_CONEXION_EXITOSA);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            GestionLog.registrar(TipoLog.DEBUG, LOG_FLUJOS_INICIALIZADOS);
        } catch (IOException e) {
            GestionLog.registrar(TipoLog.ERROR, String.format(LOG_ERROR_CONEXION, e.getMessage()));
            throw e;
        }
    }

    public void enviarJugadores(String jugador1, String jugador2) throws IOException {
        try {
            GestionLog.registrar(TipoLog.INFO, String.format(LOG_ENVIO_JUGADORES, jugador1, jugador2));
            out.writeObject(jugador1);
            out.writeObject(jugador2);
            out.flush();
            GestionLog.registrar(TipoLog.INFO, LOG_JUGADORES_ENVIADOS);
        } catch (IOException e) {
            GestionLog.registrar(TipoLog.ERROR, String.format(LOG_ERROR_ENVIO_JUGADORES, e.getMessage()));
            cerrarConexion();
            throw e;
        }
    }

    public Operacion solicitarOperacion() throws IOException, ClassNotFoundException {
        try {
            GestionLog.registrar(TipoLog.INFO, LOG_SOLICITUD_OPERACION);
            out.writeObject(SOLICITUD_OPERACION);
            out.flush();

            Operacion operacion = (Operacion) in.readObject();
            GestionLog.registrar(TipoLog.INFO, 
                String.format(LOG_OPERACION_RECIBIDA, 
                    operacion.getNum1(), 
                    operacion.getOperador(), 
                    operacion.getNum2()));
            return operacion;
        } catch (IOException | ClassNotFoundException e) {
            GestionLog.registrar(TipoLog.ERROR, String.format(LOG_ERROR_SOLICITUD_OPERACION, e.getMessage()));
            cerrarConexion();
            throw e;
        }
    }

    public boolean verificarRespuesta(int respuesta, Operacion operacion) throws IOException, ClassNotFoundException {
        try {
            GestionLog.registrar(TipoLog.DEBUG, String.format(LOG_ENVIO_RESPUESTA, respuesta));
            out.writeObject(VERIFICACION_RESPUESTA);
            out.writeObject(respuesta);
            out.flush();

            boolean esCorrecta = (Boolean) in.readObject();
            GestionLog.registrar(TipoLog.INFO, 
                String.format(LOG_RESPUESTA_VERIFICADA, 
                    esCorrecta ? TEXTO_CORRECTO : TEXTO_INCORRECTO));
            return esCorrecta;
        } catch (IOException | ClassNotFoundException e) {
            GestionLog.registrar(TipoLog.ERROR, String.format(LOG_ERROR_VERIFICACION, e.getMessage()));
            cerrarConexion();
            throw e;
        }
    }

    public void cerrarConexion() {
        GestionLog.registrar(TipoLog.INFO, LOG_CIERRE_CONEXION);
        try {
            if (in != null) {
                in.close();
                GestionLog.registrar(TipoLog.DEBUG, LOG_CIERRE_FLUJO_ENTRADA);
            }
            if (out != null) {
                out.close();
                GestionLog.registrar(TipoLog.DEBUG, LOG_CIERRE_FLUJO_SALIDA);
            }
            if (socket != null) {
                socket.close();
                GestionLog.registrar(TipoLog.DEBUG, LOG_CIERRE_SOCKET);
            }
        } catch (IOException e) {
            GestionLog.registrar(TipoLog.ERROR, String.format(LOG_ERROR_CIERRE, e.getMessage()));
        }
    }
}
