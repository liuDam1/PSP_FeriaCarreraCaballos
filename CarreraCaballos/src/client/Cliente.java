package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import model.Jugador;
import model.Carrera;
import model.Operacion;

public class Cliente {
    private static final String DIRECCION_SERVIDOR = "localhost";
    private static final int PUERTO = 12345;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Carrera carrera;
    
    public Cliente() {
        try {
            // Conectar al servidor
            socket = new Socket(DIRECCION_SERVIDOR, PUERTO);
            System.out.println("Conectado al servidor");
            
            // Inicializar flujos de entrada/salida
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void comenzarJuego(Jugador jugador1, Jugador jugador2) {
        try {
            // Enviar jugadores al servidor
            out.writeObject(jugador1);
            out.writeObject(jugador2);
            
            // Recibir estado inicial del juego
            recibirEstadoJuego();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void solicitarSiguienteTurno() {
        try {
            out.writeObject("SIGUIENTE_TURNO");
            String respuesta = (String) in.readObject();
            
            if ("OPERACION".equals(respuesta)) {
                Operacion operacion = (Operacion) in.readObject();
                // Manejar operación
            } else if ("ESTADO_JUEGO".equals(respuesta)) {
                recibirEstadoJuego();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void verificarRespuesta(int respuesta, Operacion operacion) {
        try {
            out.writeObject("VERIFICAR_RESPUESTA");
            out.writeObject(respuesta);
            out.writeObject(operacion);
            recibirEstadoJuego();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void recibirEstadoJuego() throws IOException, ClassNotFoundException {
        String respuesta = (String) in.readObject();
        
        if ("ESTADO_JUEGO".equals(respuesta)) {
            Jugador jugador1 = (Jugador) in.readObject();
            Jugador jugador2 = (Jugador) in.readObject();
            Jugador turno = (Jugador) in.readObject();
            Jugador ganador = (Jugador) in.readObject();
        }
    }
    
    public void salirJuego() {
        try {
            out.writeObject("SALIR");
            cerrar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void cerrar() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}    
