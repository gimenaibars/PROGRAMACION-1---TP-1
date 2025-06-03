package juego;

import java.awt.Color;
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
    
    //PARA AGREGAR LAS POCIONES DE PROTECCION Y VIDA
    private boolean protegido;
    private long tiempoProteccionInicio;
    private static final long DURACION_PROTECCION = 5000; 	// TIEMPO DE HALO DE PROTECCION, 5 SEGUNDOS
    private static final int RADIO_PROTECCION = 120;


    public Gondolf(double x, double y) {
        this.x = x;
        this.y = y;
        this.vida = 100;		//  CANTIDAD DE VIDA INICIAL
        this.magia = 100;		//  CANTIDAD DE MAGIA INICIAL
        this.ultimoTiempoRecargaMagia = System.currentTimeMillis();

        // CARGA LA IMAGEN DEL PERSONAJE
        this.imagen = new ImageIcon(getClass().getResource("/imagenes/gondolf.png")).getImage();
        if (imagen == null) {	//  SI LA IMAGEN NO SE PUEDE CARGAR TIRA ERROR
            System.out.println("ERROR: Imagen de Gondolf no cargada.");
        }
    
    }
  
    //  MOVIMIENTO DE GONDOLF EVITANDO CAMINAR SOBRE LAS ROCAS
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
        	
            // ESCALA AJUSTADA PARA MOSTRAR IMAGEN DEL PERSONAJE 40X40 PIXELES APROXIMADAMENTE
            double escalaX = ancho / (double) imagen.getWidth(null);
            double escalaY = alto / (double) imagen.getHeight(null);
            double escala = Math.min(escalaX, escalaY);

            entorno.dibujarImagen(imagen, x, y, 0, escala);
        } else {
            entorno.dibujarRectangulo(x, y, ancho, alto, 0, java.awt.Color.RED);
        }
        
        //  SE CREA UN EFECTO DE CIRCULOS DEGRADADOS PARA DAR EFECTO DE HALO DE PROTECCION
        if (protegido) {
            int radioMax = RADIO_PROTECCION;
            int pasos = 10;  // CANTIDAD DE CIRCULOS PARA EL DEGRADADO

            for (int i = pasos; i > 0; i--) {
                float alpha = i / (float) pasos; // VA DEL 1 AL 0.1
                int radio = radioMax * i / pasos;

                // COLOR VERDE CON TRANSPARENCIA (ALPHA ENTRE 0 Y 255)
                Color color = new Color(135, 206, 235, (int)(alpha * 20));

                entorno.dibujarCirculo(x, y, radio, color);
            }
        }
    }
    

    public boolean estaVivo() {
        return vida > 0;
    }
    
    //  RECUPERACION DE VIDA CON POCION "VIDA"
    public void recuperarVida(int cantidad) {
    	this.vida = Math.min(100, this.vida + cantidad);
    }
    
    //  ACTIVACION DE PROTECCION
    public void activarProteccion(int duracionMillis) {
        this.protegido = true;
        this.tiempoProteccionInicio = System.currentTimeMillis();
    }
  
    // CALCULA EL TIEMPO DE PROTECCION DISPONIBLE
    public boolean estaProtegido() {
        if (protegido && (System.currentTimeMillis() - tiempoProteccionInicio <= DURACION_PROTECCION)) {
            return true;
        } else {
            protegido = false;
            return false;
        }
    }


    // METODO PARA RESTAR VIDA
    public void perderVida(int cantidad) {
        vida -= cantidad;
        if (vida < 0) {
            vida = 0;
        }
    }

    public int getRadioProteccion() {
        return RADIO_PROTECCION;
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
        if (ahora - ultimoTiempoRecargaMagia >= 1000) { // CUENTA EL PASO DE 1 SEGUNDO
            magia += 5;
            if (magia > 100) magia = 100; // MAXIMO 100
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
