package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.Jugador;
import model.Operacion;

public class Cliente {
    private static final String DIRECCION_SERVIDOR = "localhost";
    private static final int PUERTO = 44444;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Cliente() throws IOException {
        socket = new Socket(DIRECCION_SERVIDOR, PUERTO);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void enviarJugadores(Jugador jugador1, Jugador jugador2) throws IOException {
        out.writeObject(jugador1);
        out.writeObject(jugador2);
    }

    public Operacion solicitarOperacion() throws IOException, ClassNotFoundException {
        out.writeObject("SOLICITAR_OPERACION");
        return (Operacion) in.readObject();
    }

    public boolean verificarRespuesta(int respuesta, Operacion operacion) throws IOException, ClassNotFoundException {
        out.writeObject("VERIFICAR_RESPUESTA");
        out.writeObject(respuesta);
        out.writeObject(operacion);
        return in.readBoolean();
    }

    public void cerrar() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
