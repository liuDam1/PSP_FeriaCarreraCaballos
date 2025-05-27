package model;

import java.io.Serializable;

public class Operacion implements Serializable{
    private int num1;
    private int num2;
    private char operador;
    private int resultado;

    public Operacion (int num1, int num2, char operador) {
        this.num1 = num1;
        this.num2 = num2;
        this.operador = operador;

        switch (operador) {
            case '+':
                resultado = num1 + num2;
                break;
            case '-':
                resultado = num1 - num2;
                break;
            case '*':
                resultado = num1 * num2;
                break;
            case '/':
                resultado = num1 / num2;
                break;
        }
    }

    public int getNum1() {
        return num1;
    }

    public int getNum2() {
        return num2;
    }

    public char getOperador() {
        return operador;
    }

    public int getResultado() {
        return resultado;
    }

    public boolean verificarResultado(int respuesta) {
        return respuesta == resultado;
    }

    @Override
    public String toString() {
        return num1 + " " + operador + " " + num2;
    }
}
