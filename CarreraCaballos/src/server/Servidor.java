package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import model.Operacion;

public class Servidor {
    // Constantes de configuración
    private static final int PUERTO = 44444;
    private static final char[] OPERADORES = { '+', '-', '*', '/' };

    // Constantes para mensajes del servidor
    private static final String MENSAJE_INICIO_SERVIDOR = "Servidor iniciado en puerto ";
    private static final String MENSAJE_CLIENTE_CONECTADO = "Cliente conectado desde ";
    private static final String MENSAJE_JUGADORES_RECIBIDOS = "Jugadores recibidos: ";
    private static final String MENSAJE_ACCION_NO_RECONOCIDA = "Servidor: Acción no reconocida - ";
    private static final String MENSAJE_ERROR_MANEJO_CLIENTE = "Servidor: Error al manejar cliente: ";
    private static final String MENSAJE_CONEXION_CERRADA = "Servidor: Conexión con cliente cerrada";
    private static final String MENSAJE_ERROR_INICIO_SERVIDOR = "Servidor: Error al iniciar servidor: ";

    // Constantes para operaciones
    private static final String SOLICITUD_OPERACION = "SOLICITAR_OPERACION";
    private static final String VERIFICACION_RESPUESTA = "VERIFICAR_RESPUESTA";
    private static final String FORMATO_OPERACION_ENVIADA = "Servidor: Enviada operación - %d %c %d";
    private static final String FORMATO_RESPUESTA_VERIFICADA = "Servidor: Respuesta %s";
    private static final String TEXTO_CORRECTA = "correcta";
    private static final String TEXTO_INCORRECTA = "incorrecta";

    // Constantes para generación de operaciones
    private static final int RANGO_NUM1 = 100;
    private static final int RANGO_NUM2 = 10;
    private static final int MINIMO_VALOR = 1;
    private static final int FACTOR_DIVISION = 10;

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println(MENSAJE_INICIO_SERVIDOR + PUERTO);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println(MENSAJE_CLIENTE_CONECTADO + socketCliente.getInetAddress());

                new Thread(() -> {
                    try (
                            ObjectOutputStream out = new ObjectOutputStream(socketCliente.getOutputStream());
                            ObjectInputStream in = new ObjectInputStream(socketCliente.getInputStream())) {

                        String jugador1 = (String) in.readObject();
                        String jugador2 = (String) in.readObject();
                        System.out.println(MENSAJE_JUGADORES_RECIBIDOS + jugador1 + " y " + jugador2);

                        String accion;
                        while ((accion = (String) in.readObject()) != null) {
                            switch (accion) {
                                case SOLICITUD_OPERACION:
                                    Operacion operacion = generarOperacion();
                                    out.writeObject(operacion);
                                    out.flush();
                                    System.out.println(String.format(FORMATO_OPERACION_ENVIADA,
                                            operacion.getNum1(),
                                            operacion.getOperador(),
                                            operacion.getNum2()));
                                    break;

                                case VERIFICACION_RESPUESTA:
                                    int respuesta = (Integer) in.readObject();
                                    Operacion op = generarOperacion();
                                    boolean esCorrecta = verificarRespuesta(respuesta, op);
                                    out.writeObject(esCorrecta);
                                    out.flush();
                                    System.out.println(String.format(FORMATO_RESPUESTA_VERIFICADA,
                                            esCorrecta ? TEXTO_CORRECTA : TEXTO_INCORRECTA));
                                    break;

                                default:
                                    System.out.println(MENSAJE_ACCION_NO_RECONOCIDA + accion);
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println(MENSAJE_ERROR_MANEJO_CLIENTE + e.getMessage());
                    } finally {
                        try {
                            socketCliente.close();
                            System.out.println(MENSAJE_CONEXION_CERRADA);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.err.println(MENSAJE_ERROR_INICIO_SERVIDOR + e.getMessage());
            e.printStackTrace();
        }
    }

    private Operacion generarOperacion() {
        Random random = new Random();
        int num1 = random.nextInt(RANGO_NUM1) + MINIMO_VALOR;
        int num2 = random.nextInt(RANGO_NUM2) + MINIMO_VALOR;
        char operador = OPERADORES[random.nextInt(OPERADORES.length)];

        if (operador == '/') {
            num2 = (num2 == 0) ? MINIMO_VALOR : num2;
            num1 = num2 * (random.nextInt(FACTOR_DIVISION) + MINIMO_VALOR);
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
