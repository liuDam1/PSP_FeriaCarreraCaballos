package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import model.Operacion;

public class Servidor {
    private static final int PUERTO = 44444;
    private static final char[] OPERADORES = { '+', '-', '*', '/' };

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en puerto " + PUERTO);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado desde " + socketCliente.getInetAddress());

                new Thread(() -> {
                    try (
                            ObjectOutputStream out = new ObjectOutputStream(socketCliente.getOutputStream());
                            ObjectInputStream in = new ObjectInputStream(socketCliente.getInputStream())) {

                        String jugador1 = (String) in.readObject();
                        String jugador2 = (String) in.readObject();
                        System.out.println("Jugadores recibidos: " + jugador1 + " y " + jugador2);

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

    private Operacion generarOperacion() {
        Random random = new Random();
        int num1 = random.nextInt(100) + 1;
        int num2 = random.nextInt(10) + 1;
        char operador = OPERADORES[random.nextInt(OPERADORES.length)];

        if (operador == '/') {
            num2 = (num2 == 0) ? 1 : num2;
            num1 = num2 * (random.nextInt(10) + 1);
        }

        return new Operacion(num1, num2, operador);
    }

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

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}    
