package es.etg.psp.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import es.etg.psp.model.Operacion;
import es.etg.psp.util.GestionLog;
import es.etg.psp.util.TipoLog;

public class Servidor {
    // Constantes de configuración
    private static final int PUERTO = 44444;
    private static final char[] OPERADORES = { '+', '-', '*', '/' };

    // Constantes para mensajes del servidor
    private static final String MENSAJE_INICIO_SERVIDOR = "Servidor iniciado en puerto ";
    private static final String MENSAJE_CLIENTE_CONECTADO = "Cliente conectado desde ";
    private static final String MENSAJE_JUGADORES_RECIBIDOS = "Jugadores recibidos: ";
    private static final String MENSAJE_ACCION_NO_RECONOCIDA = "Acción no reconocida - ";
    private static final String MENSAJE_ERROR_MANEJO_CLIENTE = "Error al manejar cliente: ";
    private static final String MENSAJE_CONEXION_CERRADA = "Conexión con cliente cerrada";
    private static final String MENSAJE_ERROR_INICIO_SERVIDOR = "Error al iniciar servidor: ";
    private static final String MENSAJE_HILO_CLIENTE_INICIADO = "Hilo para cliente iniciado";
    private static final String ERROR_CERRAR_SOCKET = "Error al cerrar socket: ";
    private static final String SEPARADOR_JUGADORES = " y ";

    // Constantes para operaciones
    private static final String SOLICITUD_OPERACION = "SOLICITAR_OPERACION";
    private static final String VERIFICACION_RESPUESTA = "VERIFICAR_RESPUESTA";
    private static final String FORMATO_OPERACION_ENVIADA = "Enviada operación - %d %c %d";
    private static final String FORMATO_RESPUESTA_VERIFICADA = "Respuesta %s";
    private static final String TEXTO_CORRECTA = "correcta";
    private static final String TEXTO_INCORRECTA = "incorrecta";
    private static final String DEBUG_OPERACION_GENERADA = "Operación generada: %s";
    private static final String DEBUG_RESPUESTA_RECIBIDA = "Respuesta recibida: %d, Operación: %s, Resultado esperado: %d";

    // Constantes para generación de operaciones
    private static final int RANGO_NUM1 = 100;
    private static final int RANGO_NUM2 = 10;
    private static final int MINIMO_VALOR = 1;
    private static final int FACTOR_DIVISION = 10;

    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            GestionLog.registrar(TipoLog.INFO, MENSAJE_INICIO_SERVIDOR + PUERTO);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                String clienteInfo = socketCliente.getInetAddress().toString();
                GestionLog.registrar(TipoLog.INFO, MENSAJE_CLIENTE_CONECTADO + clienteInfo);

                new Thread(() -> {
                    GestionLog.registrar(TipoLog.DEBUG, MENSAJE_HILO_CLIENTE_INICIADO);

                    try (
                            ObjectOutputStream out = new ObjectOutputStream(socketCliente.getOutputStream());
                            ObjectInputStream in = new ObjectInputStream(socketCliente.getInputStream())) {

                        String jugador1 = (String) in.readObject();
                        String jugador2 = (String) in.readObject();
                        GestionLog.registrar(TipoLog.INFO,
                                MENSAJE_JUGADORES_RECIBIDOS + jugador1 + SEPARADOR_JUGADORES + jugador2);

                        String accion;
                        while ((accion = (String) in.readObject()) != null) {
                            switch (accion) {
                                case SOLICITUD_OPERACION:
                                    Operacion operacion = generarOperacion();
                                    out.writeObject(operacion);
                                    out.flush();
                                    GestionLog.registrar(TipoLog.INFO,
                                            String.format(FORMATO_OPERACION_ENVIADA,
                                                    operacion.getNum1(),
                                                    operacion.getOperador(),
                                                    operacion.getNum2()));
                                    GestionLog.registrar(TipoLog.DEBUG,
                                            String.format(DEBUG_OPERACION_GENERADA, operacion));
                                    break;

                                case VERIFICACION_RESPUESTA:
                                    int respuesta = (Integer) in.readObject();
                                    Operacion op = generarOperacion();
                                    boolean esCorrecta = verificarRespuesta(respuesta, op);
                                    out.writeObject(esCorrecta);
                                    out.flush();
                                    GestionLog.registrar(TipoLog.INFO,
                                            String.format(FORMATO_RESPUESTA_VERIFICADA,
                                                    esCorrecta ? TEXTO_CORRECTA : TEXTO_INCORRECTA));
                                    GestionLog.registrar(TipoLog.DEBUG,
                                            String.format(DEBUG_RESPUESTA_RECIBIDA,
                                                    respuesta,
                                                    op,
                                                    calcularResultado(op)));
                                    break;

                                default:
                                    GestionLog.registrar(TipoLog.WARN, MENSAJE_ACCION_NO_RECONOCIDA + accion);
                            }
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        GestionLog.registrar(TipoLog.ERROR, MENSAJE_ERROR_MANEJO_CLIENTE + e.getMessage());
                    } finally {
                        try {
                            socketCliente.close();
                            GestionLog.registrar(TipoLog.INFO, MENSAJE_CONEXION_CERRADA);
                        } catch (IOException e) {
                            GestionLog.registrar(TipoLog.ERROR, ERROR_CERRAR_SOCKET + e.getMessage());
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            GestionLog.registrar(TipoLog.ERROR, MENSAJE_ERROR_INICIO_SERVIDOR + e.getMessage());
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
        return respuesta == calcularResultado(operacion);
    }

    private int calcularResultado(Operacion operacion) {
        switch (operacion.getOperador()) {
            case '+':
                return operacion.getNum1() + operacion.getNum2();
            case '-':
                return operacion.getNum1() - operacion.getNum2();
            case '*':
                return operacion.getNum1() * operacion.getNum2();
            case '/':
                return operacion.getNum1() / operacion.getNum2();
            default:
                return 0;
        }
    }

    public static void main(String[] args) {
        new Servidor().iniciar();
    }
}
