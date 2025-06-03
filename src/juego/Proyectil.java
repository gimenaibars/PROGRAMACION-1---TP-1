package juego;

import java.awt.Color;
import entorno.Entorno;

public class Proyectil {
    private double x, y;
    private double vx, vy;
    private boolean activo;

    public Proyectil(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.activo = true;
        
    }
    

    public void mover() {
        if (activo) {
            x += vx;
            y += vy;
            if (x < 0 || x > 600 || y < 0 || y > 600) activo = false;
        }
    }

    public boolean colisionaCon(Gondolf gondolf) {
        double distancia = Math.hypot(gondolf.getX() - x, gondolf.getY() - y);

        if (gondolf.estaProtegido()) {
            return distancia < gondolf.getRadioProteccion();
        } else {
            return distancia < 20;	// IMPACTA CON GONDOLF A MENOS DE 20 PIXELES DE DISTANCIA
        }
    }
    

    public void dibujar(Entorno entorno) {
        if (activo) {
            entorno.dibujarCirculo((int)x, (int)y, 10, Color.MAGENTA);
        }
    }

    public boolean estaActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
