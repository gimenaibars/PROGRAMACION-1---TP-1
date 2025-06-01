package juego;

import java.awt.Image;
import javax.swing.ImageIcon;
import entorno.Entorno;

public class Roca {
    private double x, y;
    private final int ancho = 50;
    private final int alto = 50;
    private Image imagen;

    public Roca(double x, double y) {
        this.x = x;
        this.y = y;
        this.imagen = new ImageIcon(getClass().getResource("/imagenes/roca.png")).getImage();

    }

    public void dibujar(Entorno entorno) {
        // Escala si la imagen no es 50x50
        double escalaX = (double) ancho / imagen.getWidth(null);
        double escalaY = (double) alto / imagen.getHeight(null);
        double escala = Math.min(escalaX, escalaY);

        entorno.dibujarImagenConCentro(
            imagen,
            x, y,
            imagen.getWidth(null) / 2.0,
            imagen.getHeight(null) / 2.0,
            0,
            escala
        );
    }

    // Colisión simple rectangular
    public boolean colisiona(double xObjeto, double yObjeto, int anchoObjeto, int altoObjeto) {
        return (Math.abs(x - xObjeto) * 2 < (ancho + anchoObjeto)) &&
               (Math.abs(y - yObjeto) * 2 < (alto + altoObjeto));
    }
}
