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
    private static final int PUERTO = 44444;
    private Carrera carrera;

    public static void main(String[] args) {
        new Servidor().iniciar();
    }

    public void iniciar() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);

            while (true) {
                Socket cliente = servidor.accept();
                new Thread(() -> manejarCliente(cliente)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manejarCliente(Socket cliente) {
        try (ObjectOutputStream out = new ObjectOutputStream(cliente.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(cliente.getInputStream())) {

            // Recibir jugadores
            Jugador jugador1 = (Jugador) in.readObject();
            Jugador jugador2 = (Jugador) in.readObject();
            carrera = new Carrera(jugador1, jugador2);

            // Manejar solicitudes
            while (true) {
                String comando = (String) in.readObject();
                
                switch (comando) {
                    case "SOLICITAR_OPERACION":
                        out.writeObject(carrera.generarOperacion());
                        break;
                        
                    case "VERIFICAR_RESPUESTA":
                        int respuesta = (int) in.readObject();
                        Operacion operacion = (Operacion) in.readObject();
                        boolean correcto = operacion.verificarResultado(respuesta);
                        out.writeBoolean(correcto);
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error con cliente: " + e.getMessage());
        }
    }
}
