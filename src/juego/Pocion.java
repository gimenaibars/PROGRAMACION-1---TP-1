package juego;

import entorno.Entorno;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Pocion {
    public enum Tipo { VIDA, PROTECCION }

    private double x, y;
    private Tipo tipo;
    private boolean activa;
    private Image imagen;

    // FUNCION PARA UBICACION DE LA POCION Y NOMBRE
    public Pocion(double x, double y, String string) {
        this.x = x;
        this.y = y;
        this.tipo = string.equalsIgnoreCase("vida") ? Tipo.VIDA : Tipo.PROTECCION;
        this.activa = true;

        if (this.tipo == Tipo.VIDA) {
            imagen = new ImageIcon(getClass().getResource("/imagenes/pocion_vida.png")).getImage();
        } else {
            imagen = new ImageIcon(getClass().getResource("/imagenes/pocion_proteccion.png")).getImage();
        }
    }



    public void dibujar(Entorno entorno) {
        entorno.dibujarImagen(imagen, x, y, 0, 0.02);
    }

    public boolean estaActiva() {
        return activa;
    }

    public void desactivar() {
        activa = false;
    }

    public boolean colisionaCon(Gondolf g) {
        return Math.hypot(g.getX() - x, g.getY() - y) < 30;		//  CALCULA LA COLISION CON GONDOLF MENOS DE 30 PIXELES
    }

    public Tipo getTipo() {
        return tipo;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
