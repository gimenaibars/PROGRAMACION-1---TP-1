package juego;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import entorno.Entorno;

public class JefeFinal {
    private double x, y;
    private int vida;
    private Image imagen;
    private boolean visible;
    private long tiempoCambioVisibilidad;
    private boolean enModoVisible;

    private long ultimoAtaqueDistancia;
    private long ultimoAtaqueArea;
    private long ultimoAtaqueBomba;
    private long ultimoAtaqueAbanico;

    private static final long TIEMPO_VISIBLE = 3000;		// TIEMPO DEVISIBILIDAD DEL JEFE
    private static final long TIEMPO_INVISIBLE = 2000;		// TIEMPO DE INVISIBILIDAD DEL JEFE
    private static final long INTERVALO_ATAQUE_DISTANCIA = 4000;
    private static final long INTERVALO_ATAQUE_AREA = 6000;
    private static final long INTERVALO_ATAQUE_BOMBA = 7000;	// ATAQUE DE BOMBA CADA 7 SEGUNDOS
    private static final long INTERVALO_ATAQUE_ABANICO = 8000; // ATAQUE ABANICO CADA 8 SEGUNDOS
    
    private boolean entrando = true;
    private double yObjetivo = 200;

    private Random random = new Random();		// DEVUELVE DE MANERA ALEATORIA
    private ArrayList<Proyectil> proyectiles = new ArrayList<>();	//LISTA DE PROYECTILES

    // EFECTO VISUAL Y DAÑO DE BOMBA
    private Bomba bomba = null;

    public JefeFinal(double x, double y) {
        this.x = x;
        this.y = y;
        this.vida = 100;		// VIDA INICIAL DEL JEFE
        this.visible = true;
        this.enModoVisible = true;
        this.imagen = new ImageIcon(getClass().getResource("/imagenes/jefe_final.png")).getImage();
        this.ultimoAtaqueDistancia = System.currentTimeMillis();
        this.ultimoAtaqueArea = System.currentTimeMillis();
        this.ultimoAtaqueBomba = System.currentTimeMillis();
        this.ultimoAtaqueAbanico = System.currentTimeMillis();
        this.tiempoCambioVisibilidad = System.currentTimeMillis();
    }

    public void recibirDaño(int daño) {
        if (visible && estaVivo()) {
            this.vida -= daño;
            if (vida < 0) vida = 0;
        }
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public boolean estaVisible() {
        return visible;
    }

    public void actualizarEstado(Gondolf gondolf) {
        long ahora = System.currentTimeMillis();
        
        if (entrando) {
            y += 2; // VELOCIDAD DE ENTRADA

            if (y >= yObjetivo) {
                y = yObjetivo;
                entrando = false;
            }
        } else {
            // MOVIMIENTO DEL JEFE (INESTABLE)
            double deltaX = Math.sin(System.nanoTime() / 300_000_000.0) * 2;
            x += deltaX;
        }


        // VISIBILIDAD - ALTERNA ENTRE 3 SEGUNDOS VISIBLE Y 2 SEGUNDOS INVISIBLE
        if (enModoVisible && ahora - tiempoCambioVisibilidad >= TIEMPO_VISIBLE) {
            visible = false;
            enModoVisible = false;
            tiempoCambioVisibilidad = ahora;
        } else if (!enModoVisible && ahora - tiempoCambioVisibilidad >= TIEMPO_INVISIBLE) {
            x = 50 + random.nextInt(500);
            y = 50 + random.nextInt(500);
            visible = true;
            enModoVisible = true;
            tiempoCambioVisibilidad = ahora;
        }

        if (visible && estaVivo()) {
            // MUEVE Y LIMPIA PROYECTILES
            proyectiles.removeIf(p -> !p.estaActivo());
            for (Proyectil p : proyectiles) {
                p.mover();
                if (p.colisionaCon(gondolf)) {
                    gondolf.perderVida(5);
                    p.setActivo(false);
                }
            }

            // ATAQUES
            if (ahora - ultimoAtaqueDistancia >= INTERVALO_ATAQUE_DISTANCIA) {
                dispararProyectil(gondolf);
                ultimoAtaqueDistancia = ahora;
            }

            if (ahora - ultimoAtaqueArea >= INTERVALO_ATAQUE_AREA) {
                ataqueArea(gondolf);
                ultimoAtaqueArea = ahora;
            }

            if (ahora - ultimoAtaqueBomba >= INTERVALO_ATAQUE_BOMBA) {
                lanzarBomba(gondolf);
                ultimoAtaqueBomba = ahora;
            }

            if (ahora - ultimoAtaqueAbanico >= INTERVALO_ATAQUE_ABANICO) {
                lanzarProyectilesEnAbanico();
                ultimoAtaqueAbanico = ahora;
            }

            moverAleatoriamente();
        }

        // ACTUALIZA BOMBAS SI ESTA ACTIVO
        if (bomba != null && bomba.estaActiva()) {
            bomba.actualizar(gondolf);
        } else {
            bomba = null;
        }

        // CONDICIONES DE FIN DE JUEGO
        if (!gondolf.estaVivo()) {
            System.out.println("¡Has perdido! Gondolf ha muerto.");
        }

        if (!estaVivo()) {
            System.out.println("¡Has ganado! El jefe final ha sido derrotado.");
        }
    }

    private void dispararProyectil(Gondolf gondolf) {
        double dx = gondolf.getX() - x;
        double dy = gondolf.getY() - y;
        double distancia = Math.hypot(dx, dy);
        double velocidad = 5;
        double vx = (dx / distancia) * velocidad;
        double vy = (dy / distancia) * velocidad;
        proyectiles.add(new Proyectil(x, y, vx, vy));
    }

    // ATAQUE EN ABANICO
    private void lanzarProyectilesEnAbanico() {
        int cantidad = 12;
        double velocidad = 4;
        for (int i = 0; i < cantidad; i++) {
            double angulo = 2 * Math.PI / cantidad * i;
            double vx = velocidad * Math.cos(angulo);
            double vy = velocidad * Math.sin(angulo);
            proyectiles.add(new Proyectil(x, y, vx, vy));
        }
    }

    private void ataqueArea(Gondolf gondolf) {
        double distanciaJugador = Math.hypot(gondolf.getX() - x, gondolf.getY() - y);
        int radioAtaque = 80;
        if (distanciaJugador <= radioAtaque) {
            gondolf.perderVida(20);
        }
    }

    private void lanzarBomba(Gondolf gondolf) {
        bomba = new Bomba(gondolf.getX(), gondolf.getY(), 70, 60);
    }

    private void moverAleatoriamente() {
        double dx = (random.nextDouble() - 0.5) * 2;
        double dy = (random.nextDouble() - 0.5) * 2;
        x = Math.min(Math.max(x + dx, 50), 550);
        y = Math.min(Math.max(y + dy, 50), 550);
    }

    public void dibujar(Entorno entorno) {
        if (!estaVivo()) return;

        if (!visible) {
            return;
        }

        entorno.dibujarImagen(imagen, x, y, 0, 0.01);

        // BARRA DE VIDA DE JEFE
        int anchoBarra = 60;
        int altoBarra = 5;
        int vidaAncho = (int)(anchoBarra * vida / 100.0);
        entorno.dibujarRectangulo(x, y - 30, anchoBarra, altoBarra, 0, Color.BLACK);
        entorno.dibujarRectangulo(x - (anchoBarra - vidaAncho) / 2.0, y - 30, vidaAncho, altoBarra, 0, Color.RED);

        // DIBUJA PROYECTILES
        for (Proyectil p : proyectiles) {
            p.dibujar(entorno);
        }

        // DIBUJA BOMBA SI ESTA ACTIVA
        if (bomba != null && bomba.estaActiva()) {
            bomba.dibujar(entorno);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getVida() { return vida; }
}
