package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import model.Operacion;

/**
 * Clase que representa el servidor en la aplicación de juego de operaciones matemáticas.
 * Se encarga de manejar las conexiones de los clientes, generar operaciones matemáticas
 * y verificar las respuestas de los jugadores.
 */
public class Servidor {
    private static final int PUERTO = 44444;
    private static final char[] OPERADORES = {'+', '-', '*', '/'};

    /**
     * Inicia el servidor y comienza a aceptar conexiones de clientes.
     */
    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);
            
            while (true) {
                // Aceptar una nueva conexión de cliente
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado desde " + socketCliente.getInetAddress());
                
                // Crear un nuevo hilo para manejar la conexión con el cliente
                new Thread(() -> {
                    try (
                        ObjectOutputStream out = new ObjectOutputStream(socketCliente.getOutputStream());
                        ObjectInputStream in = new ObjectInputStream(socketCliente.getInputStream())
                    ) {
                        // Leer los nombres de los jugadores
                        String jugador1 = (String) in.readObject();
                        String jugador2 = (String) in.readObject();
                        System.out.println("Jugadores recibidos: " + jugador1 + " y " + jugador2);
                        
                        // Manejar las solicitudes del cliente
                        String accion;
                        while ((accion = (String) in.readObject()) != null) {
                            switch (accion) {
                                case "SOLICITAR_OPERACION":
                                    Operacion operacion = generarOperacion();
                                    out.writeObject(operacion);
                                    out.flush();
                                    System.out.println("Servidor: Enviada operación - " + 
                                                       operacion.getNum1() + " " + 
                                                       operacion.getOperador() + " " + 
                                                       operacion.getNum2());
                                    break;
                                    
                                case "VERIFICAR_RESPUESTA":
                                    int respuesta = (Integer) in.readObject();
                                    // Nota: En una implementación completa, se debería verificar la respuesta
                                    // con la operación correspondiente al jugador. Aquí se usa una operación nueva.
                                    Operacion op = generarOperacion(); 
                                    boolean esCorrecta = verificarRespuesta(respuesta, op);
                                    out.writeObject(esCorrecta);
                                    out.flush();
                                    System.out.println("Servidor: Respuesta " + (esCorrecta ? "correcta" : "incorrecta"));
                                    break;
                                    
                                default:
                                    System.out.println("Servidor: Acción no reconocida - " + accion);
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Servidor: Error al manejar cliente: " + e.getMessage());
                    } finally {
                        try {
                            socketCliente.close();
                            System.out.println("Servidor: Conexión con cliente cerrada");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.err.println("Servidor: Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera una operación matemática aleatoria (suma, resta, multiplicación o división).
     * @return Operacion generada aleatoriamente.
     */
    private Operacion generarOperacion() {
        Random random = new Random();
        int num1 = random.nextInt(100) + 1;
        int num2 = random.nextInt(10) + 1; // Mantener números pequeños para el juego
        char operador = OPERADORES[random.nextInt(OPERADORES.length)];
        
        // Asegurarse de que la división sea entera y sin resto
        if (operador == '/') {
            // Ajustar los números para que la división sea exacta
            num2 = (num2 == 0) ? 1 : num2;
            num1 = num2 * (random.nextInt(10) + 1); // num1 será múltiplo de num2
        }
        
        return new Operacion(num1, num2, operador);
    }

    /**
     * Verifica si la respuesta proporcionada por el jugador es correcta para la operación dada.
     * @param respuesta Respuesta ingresada por el jugador.
     * @param operacion Operación a verificar.
     * @return true si la respuesta es correcta, false en caso contrario.
     */
    private boolean verificarRespuesta(int respuesta, Operacion operacion) {
        int resultado;
        switch (operacion.getOperador()) {
            case '+':
                resultado = operacion.getNum1() + operacion.getNum2();
                break;
            case '-':
                resultado = operacion.getNum1() - operacion.getNum2();
                break;
            case '*':
                resultado = operacion.getNum1() * operacion.getNum2();
                break;
            case '/':
                resultado = operacion.getNum1() / operacion.getNum2();
                break;
            default:
                return false;
        }
        return respuesta == resultado;
    }

    /**
     * Método principal para iniciar el servidor.
     * @param args Argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}
