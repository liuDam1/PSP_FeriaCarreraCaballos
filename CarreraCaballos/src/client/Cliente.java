package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import model.Operacion;

public class Cliente {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static final String DIRECCION_SERVIDOR = "localhost";
    private static final int PUERTO = 44444;

    public Cliente() throws IOException {
        try {
            System.out.println("Cliente: Intentando conectar a " + DIRECCION_SERVIDOR + ":" + PUERTO);
            socket = new Socket(DIRECCION_SERVIDOR, PUERTO);
            System.out.println("Cliente: Conexión establecida con éxito");

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Cliente: Flujos de entrada/salida inicializados");
        } catch (IOException e) {
            System.err.println("Cliente: Error al conectar: " + e.getMessage());
            throw e;
        }
    }

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

    public void cerrarConexion() {
        System.out.println("Cliente: Cerrando conexión");
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
