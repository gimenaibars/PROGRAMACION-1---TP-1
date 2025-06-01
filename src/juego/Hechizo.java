package juego;

import java.awt.Color;
import java.util.ArrayList;

public class Hechizo {
    private String nombre;
    private int area;
    private int costoMagia;
    private Color color;

    public Hechizo(String nombre, int area, int costoMagia, Color color) {
        this.nombre = nombre;
        this.area = area;
        this.costoMagia = costoMagia;
        this.color = color;
    }

 // Hechizo.java
    public void lanzar(int x, int y, ArrayList<Murcielago> murcielagos, Gondolf gondolf) {
        long tiempoActual = System.currentTimeMillis();
        
        // Limites de la ventana visible (pueden ser constantes)
        int anchoVisible = 600;
        int altoVisible = 600;
        
        if (nombre.equals("Hielo")) {
            double rango = this.area;  // usa el atributo area, ej: 80
            
            for (Murcielago m : murcielagos) {
                if (m.estaVivo()) {
                    // Verifico que el murcielago esté dentro de la ventana visible
                    if (m.getX() >= 0 && m.getX() <= anchoVisible && m.getY() >= 0 && m.getY() <= altoVisible) {
                        double dx = m.getX() - x;
                        double dy = m.getY() - y;
                        double distancia = Math.hypot(dx, dy);

                        if (distancia <= rango) {
                            m.congelar(tiempoActual);
                        }
                    }
                }
            }
        } else if (nombre.equals("Fuego")) {
            double rango = this.area; // rango del fuego
            for (Murcielago m : murcielagos) {
                if (m.estaVivo()) {
                    if (m.getX() >= 0 && m.getX() <= anchoVisible && m.getY() >= 0 && m.getY() <= altoVisible) {
                        double dx = m.getX() - x;
                        double dy = m.getY() - y;
                        double distancia = Math.sqrt(dx * dx + dy * dy);

                        if (distancia <= rango) {
                            m.quemar(tiempoActual);
                            m.morir();
                        }
                    }
                }
            }
        }
    }


    public String getNombre() { return nombre; }
    public int getArea() { return area; }
    public int getCostoMagia() { return costoMagia; }
    public Color getColor() { return color; }
}
