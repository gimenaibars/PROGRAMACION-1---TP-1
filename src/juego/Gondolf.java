package juego;

import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import entorno.Entorno;

public class Gondolf {
    private double x, y;
    private int vida;
    private int magia;
    private long ultimoTiempoRecargaMagia;
    private final int ancho = 40;
    private final int alto = 40;
    private Image imagen;

    public Gondolf(double x, double y) {
        this.x = x;
        this.y = y;
        this.vida = 100;
        this.magia = 100;
        this.ultimoTiempoRecargaMagia = System.currentTimeMillis();

        // Cargar imagen del personaje
        this.imagen = new ImageIcon(getClass().getResource("/imagenes/gondolf.png")).getImage();

        if (imagen == null) {
            System.out.println("ERROR: Imagen de Gondolf no cargada.");
        }
        
        
    }

    
    public void mover(double dx, double dy, ArrayList<Roca> rocas) {
        double nuevaX = x + dx;
        double nuevaY = y + dy;

        if (nuevaX - ancho/2 >= 0 && nuevaX + ancho/2 <= Juego.WIDTH_JUEGO) {
            boolean choca = false;
            for (Roca r : rocas) {
                if (r.colisiona(nuevaX, nuevaY, ancho, alto)) {
                    choca = true;
                    break;
                }
            }
            if (!choca) {
                x = nuevaX;
            }
        }

        if (nuevaY - alto/2 >= 0 && nuevaY + alto/2 <= Juego.HEIGHT) {
            boolean choca = false;
            for (Roca r : rocas) {
                if (r.colisiona(x, nuevaY, ancho, alto)) {
                    choca = true;
                    break;
                }
            }
            if (!choca) {
                y = nuevaY;
            }
        }
    }

    public void dibujar(Entorno entorno) {
        if (imagen != null) {
            // Escala ajustada para tamaño aprox 40x40
            double escalaX = ancho / (double) imagen.getWidth(null);
            double escalaY = alto / (double) imagen.getHeight(null);
            double escala = Math.min(escalaX, escalaY);

            entorno.dibujarImagen(imagen, x, y, 0, escala);
        } else {
            entorno.dibujarRectangulo(x, y, ancho, alto, 0, java.awt.Color.RED);
        }
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    // Método para restar vida
    public void perderVida(int cantidad) {
        vida -= cantidad;
        if (vida < 0) {
            vida = 0;
        }
    }

    public void perderVidaPorcentaje(double porcentaje) {
        int cantidad = (int)(vida * porcentaje);
        perderVida(cantidad);
    }

    public int getVida() {
        return vida;
    }

    public int getMagia() {
        return magia;
    }

    public boolean puedeUsarHechizo(int costo) {
        return magia >= costo;
    }
    
    public void consumirMagia(int costo) {
        if (magia >= costo) {
            magia -= costo;
            if (magia < 0) magia = 0;
        }
    }

    public void regenerarMagia() {
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoTiempoRecargaMagia >= 1000) { // 1 segundo pasó
            magia += 5;
            if (magia > 100) magia = 100; // Máximo 100
            ultimoTiempoRecargaMagia = ahora;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
