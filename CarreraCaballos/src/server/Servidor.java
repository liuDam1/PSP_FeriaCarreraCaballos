package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import model.Jugador;
import model.Carrera;
import model.Operacion;

public class Servidor {
    private static final int PUERTO = 12345;
    private ServerSocket servidorSocket;
    private Socket socketCliente;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Carrera carrera;
    
    public Servidor() {
        try {
            servidorSocket = new ServerSocket(PUERTO);
            System.out.println("Servidor iniciado en el puerto " + PUERTO);
            
            // Esperar a que un cliente se conecte
            socketCliente = servidorSocket.accept();
            System.out.println("Cliente conectado");
            
            // Inicializar flujos de entrada/salida
            out = new ObjectOutputStream(socketCliente.getOutputStream());
            in = new ObjectInputStream(socketCliente.getInputStream());
            
            // Comenzar a manejar solicitudes del cliente
            manejarCliente();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrar();
        }
    }
    
    private void manejarCliente() {
        try {
            // Recibir jugadores desde el cliente
            Jugador jugador1 = (Jugador) in.readObject();
            Jugador jugador2 = (Jugador) in.readObject();
            
            // Crear carrera
            carrera = new Carrera(jugador1, jugador2);
            
            // Enviar estado inicial al cliente
            enviarEstadoJuego();
            
            // Procesar solicitudes del cliente
            while (true) {
                String solicitud = (String) in.readObject();
                
                if ("SIGUIENTE_TURNO".equals(solicitud)) {
                    manejarSiguienteTurno();
                } else if ("VERIFICAR_RESPUESTA".equals(solicitud)) {
                    int respuesta = (int) in.readObject();
                    manejarVerificarRespuesta(respuesta);
                } else if ("SALIR".equals(solicitud)) {
                    break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void manejarSiguienteTurno() {
        try {
            Jugador jugadorActual = carrera.getTurno();
            
            if (carrera.hayOperacion()) {
                Operacion operacion = carrera.generarOperacion();
                out.writeObject("OPERACION");
                out.writeObject(operacion);
            } else {
                jugadorActual.sumarPuntos(10);
                carrera.cambiarTurno();
                enviarEstadoJuego();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void manejarVerificarRespuesta(int respuesta) {
        try {
            Jugador jugadorActual = carrera.getTurno();
            Operacion operacion = (Operacion) in.readObject();
            
            int puntos = 10;
            if (operacion.verificarResultado(respuesta)) {
                puntos += 5;
            }
            
            jugadorActual.sumarPuntos(puntos);
            carrera.cambiarTurno();
            enviarEstadoJuego();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void enviarEstadoJuego() throws IOException {
        out.writeObject("ESTADO_JUEGO");
        out.writeObject(carrera.getJugador1());
        out.writeObject(carrera.getJugador2());
        out.writeObject(carrera.getTurno());
        out.writeObject(carrera.hayGanador() ? carrera.getGanador() : null);
    }
    
    private void cerrar() {
        try {
            in.close();
            out.close();
            socketCliente.close();
            servidorSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Servidor();
    }
}    
