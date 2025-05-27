package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.Operacion;

/**
 * Clase que representa el cliente en la aplicación de juego de operaciones matemáticas.
 * Se encarga de establecer la conexión con el servidor y enviar/receive datos durante el juego.
 */
public class Cliente {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final String DIRECCION_SERVIDOR = "localhost";
    private static final int PUERTO = 44444;

    /**
     * Constructor de la clase Cliente.
     * Establece la conexión con el servidor y crea los flujos de entrada y salida.
     * @throws IOException Si ocurre un error al establecer la conexión.
     */
    public Cliente() throws IOException {
        try {
            System.out.println("Cliente: Intentando conectar a " + DIRECCION_SERVIDOR + ":" + PUERTO);
            socket = new Socket(DIRECCION_SERVIDOR, PUERTO);
            System.out.println("Cliente: Conexión establecida con éxito");
            
            // Crear flujos de entrada y salida para la comunicación con el servidor
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Cliente: Flujos de entrada/salida inicializados");
        } catch (IOException e) {
            System.err.println("Cliente: Error al conectar: " + e.getMessage());
            throw e; // Re-lanza la excepción para que sea manejada por el llamador
        }
    }

    /**
     * Envía los nombres de los jugadores al servidor.
     * @param jugador1 Nombre del primer jugador.
     * @param jugador2 Nombre del segundo jugador.
     * @throws IOException Si ocurre un error al enviar los datos.
     */
    public void enviarJugadores(String jugador1, String jugador2) throws IOException {
        try {
            System.out.println("Cliente: Enviando jugadores - " + jugador1 + " y " + jugador2);
            out.writeObject(jugador1);
            out.writeObject(jugador2);
            out.flush();
            System.out.println("Cliente: Jugadores enviados correctamente");
        } catch (IOException e) {
            System.err.println("Cliente: Error al enviar jugadores: " + e.getMessage());
            cerrarConexion();
            throw e;
        }
    }

    /**
     * Solicita una nueva operación matemática al servidor.
     * @return Operacion generada por el servidor.
     * @throws IOException Si ocurre un error de comunicación.
     * @throws ClassNotFoundException Si no se encuentra la clase Operacion al recibir datos.
     */
    public Operacion solicitarOperacion() throws IOException, ClassNotFoundException {
        try {
            System.out.println("Cliente: Solicitando operación");
            out.writeObject("SOLICITAR_OPERACION");
            out.flush();
            
            Operacion operacion = (Operacion) in.readObject();
            System.out.println("Cliente: Operación recibida - " + 
                              operacion.getNum1() + " " + 
                              operacion.getOperador() + " " + 
                              operacion.getNum2());
            return operacion;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Cliente: Error al solicitar operación: " + e.getMessage());
            cerrarConexion();
            throw e;
        }
    }

    /**
     * Envía la respuesta del jugador al servidor y verifica si es correcta.
     * @param respuesta Respuesta ingresada por el jugador.
     * @param operacion Operación actual que se está resolviendo.
     * @return true si la respuesta es correcta, false en caso contrario.
     * @throws IOException Si ocurre un error de comunicación.
     * @throws ClassNotFoundException Si no se encuentra la clase al recibir datos.
     */
    public boolean verificarRespuesta(int respuesta, Operacion operacion) throws IOException, ClassNotFoundException {
        try {
            System.out.println("Cliente: Enviando respuesta - " + respuesta);
            out.writeObject("VERIFICAR_RESPUESTA");
            out.writeObject(respuesta);
            out.flush();
            
            boolean esCorrecta = (Boolean) in.readObject();
            System.out.println("Cliente: Respuesta " + (esCorrecta ? "correcta" : "incorrecta"));
            return esCorrecta;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Cliente: Error al verificar respuesta: " + e.getMessage());
            cerrarConexion();
            throw e;
        }
    }

    /**
     * Cierra la conexión con el servidor y libera los recursos utilizados.
     */
    public void cerrarConexion() {
        System.out.println("Cliente: Cerrando conexión");
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
