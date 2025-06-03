package juego;

import java.awt.Color;
import java.util.ArrayList;

public class Hechizo {
    private String nombre;
    private int area;
    private int costoMagia;
    private int daño;     
    private Color color;

    public Hechizo(String nombre, int area, int costoMagia, int daño, Color color) {
        this.nombre = nombre;
        this.area = area;
        this.costoMagia = costoMagia;
        this.daño = daño;    
        this.color = color;
        
    }

    public void lanzar(int x, int y, ArrayList<Murcielago> murcielagos, Gondolf gondolf) {
        long tiempoActual = System.currentTimeMillis();
        int anchoVisible = 600;
        int altoVisible = 600;

        if (nombre.equals("Hielo")) {
            double rango = this.area;
            for (Murcielago m : murcielagos) {
                if (m.estaVivo() && m.getX() >= 0 && m.getX() <= anchoVisible && m.getY() >= 0 && m.getY() <= altoVisible) {
                    double dx = m.getX() - x;
                    double dy = m.getY() - y;
                    double distancia = Math.hypot(dx, dy);

                    if (distancia <= rango) {
                        m.congelar(tiempoActual);
                    }
                }
            }
        } else if (nombre.equals("Fuego")) {
            double rango = this.area;
            for (Murcielago m : murcielagos) {
                if (m.estaVivo() && m.getX() >= 0 && m.getX() <= anchoVisible && m.getY() >= 0 && m.getY() <= altoVisible) {
                    double dx = m.getX() - x;
                    double dy = m.getY() - y;
                    double distancia = Math.sqrt(dx * dx + dy * dy);

                    if (distancia <= rango) {
                        m.quemar(tiempoActual);
                        m.marcarComoEliminadoPorJugador();
                        m.morir();
                    }
                }
            }
        }
    }

    public String getNombre() { return nombre; }
    public int getArea() { return area; }
    public int getCostoMagia() { return costoMagia; }
    public int getDaño() { return daño; } 
    public Color getColor() { return color; }
}
