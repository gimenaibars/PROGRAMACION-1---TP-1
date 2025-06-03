package juego;

import java.awt.Image;
import javax.swing.ImageIcon;
import entorno.Entorno;

public class Murcielago {
    private double x, y;
    private int ancho = 30;
    private int alto = 30;
    private boolean vivo = true;
    private Image imagen;
    private Image imagenCongelado;
    private Image imagenQuemado;
    private boolean congelado = false;
    private long tiempoDescongelacion = 0;
    private boolean quemado = false;
    private long tiempoDesquemado = 0;
    private long muertoVisibleHasta = 0;
    private boolean eliminadoPorJugador = false; 
    
    public Murcielago(double x, double y) {
        this.x = x;
        this.y = y;

        this.imagen = new ImageIcon(getClass().getResource("/imagenes/murcielago.png")).getImage();
        this.imagenCongelado = new ImageIcon(getClass().getResource("/imagenes/murcielago_hielo.png")).getImage();
        this.imagenQuemado = new ImageIcon(getClass().getResource("/imagenes/murcielago_fuego.png")).getImage();
    }
  
    public void marcarComoEliminadoPorJugador() {
    	this.eliminadoPorJugador = true;
    }
    public boolean fueEliminadoPorJugador() {
    	return this.eliminadoPorJugador;
    }
   
    // CUANDO LA FUNCION ESTA ACTIVA DURA 3 SEGUNDOS
    public void congelar(long tiempoActual) {
        congelado = true;
        tiempoDescongelacion = tiempoActual + 3000;
    }
    
    public void eliminarSinAnimacion() {
        vivo = false;
        congelado = false;
        quemado = false;
        
    }
    
    // CUANDO LA FUNCION ESTA ACTIVA MUESTRA LA IMAGEN DE FUEGO POR 2 SEGUNDOS
    public void quemar(long ahora) {
        quemado = true;
        tiempoDesquemado = ahora + 2000; 
        if (vivo) {
            vivo = false;
        }
    }

    public boolean estaVivo() {
        return vivo || estaQuemado();
    }

    public boolean estaCongelado() {
        return congelado;
    }

    public boolean estaQuemado() {
        return quemado && System.currentTimeMillis() < tiempoDesquemado;
    }

    public void actualizarEstado(long tiempoActual) {
        if (congelado && tiempoActual >= tiempoDescongelacion) {
            congelado = false;
        }
        if (quemado && tiempoActual >= tiempoDesquemado) {
            quemado = false;
        }
    }
    
    public void perseguir(Gondolf gondolf, double velocidad) {
        if (!vivo || congelado) return;

        double dx = gondolf.getX() - x;
        double dy = gondolf.getY() - y;
        double distancia = Math.sqrt(dx*dx + dy*dy);
   
        if (distancia > 0) {
            this.x += (dx / distancia) * velocidad;
            this.y += (dy / distancia) * velocidad;
        }
    }

    public void dibujar(Entorno entorno) {
        if (estaVivo()) {
            Image img;
            if (congelado && imagenCongelado != null) {
                img = imagenCongelado;
            } else if (estaQuemado() && imagenQuemado != null) {
                img = imagenQuemado;
            } else {
                img = imagen;
            }

            if (img != null) {
                double escalaX = ancho / (double) img.getWidth(null);
                double escalaY = alto / (double) img.getHeight(null);
                double escala = Math.min(escalaX, escalaY);

                entorno.dibujarImagenConCentro(img, x, y,
                    img.getWidth(null)/2.0,
                    img.getHeight(null)/2.0,
                    0, escala);
            }
        }
    }
    
    public void moverAfueraDe(double x, double y) {
        double dx = this.x - x;
        double dy = this.y - y;
        double distancia = Math.hypot(dx, dy);

        if (distancia != 0) {
            this.x += (dx / distancia) * 1.5;
            this.y += (dy / distancia) * 1.5;
        }
    }

    // RANGO DONDE ACTUA LA COLISION CONTRA GONDOLF
    public boolean colisiona(Gondolf gondolf) {
        final int gondolfAncho = 40;
        final int gondolfAlto = 40;
        return (Math.abs(gondolf.getX() - x) * 2 < (ancho + gondolfAncho)) &&
               (Math.abs(gondolf.getY() - y) * 2 < (alto + gondolfAlto));
    }

    // ACCION DEL MURCIELAGO CUANDO COLISIONA CON GONDOLF
    public void atacarSiColisiona(Gondolf gondolf) {
        if (vivo && colisiona(gondolf)) {
            gondolf.perderVida(10);  // QUITA 10 PUNTOS DE VIDA A GONDOLF
            this.eliminarSinAnimacion();  // MURCIELAGO MUERE INMEDIATAMENTE SIN ANIMACION
        }
    }

		
    public void morir() {
        if (vivo) {
            vivo = false;
            if (!quemado) {
                quemar(System.currentTimeMillis());
            }
            muertoVisibleHasta = System.currentTimeMillis() + 300; // MUESTRA POR 0.3 SEGUNDOS
        }
    }

    public long getMuertoVisibleHasta() {
        return muertoVisibleHasta;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
