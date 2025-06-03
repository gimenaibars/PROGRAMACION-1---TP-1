package juego;

import java.awt.Color;
import entorno.Entorno;

public class Bomba {
    private double x, y;
    private int radioMaximo;
    private double radioActual;
    private int duracionTicks;
    private int ticks;
    private boolean activa;

    public Bomba(double x, double y, int radioMaximo, int duracionTicks) {
        this.x = x;
        this.y = y;
        this.radioMaximo = radioMaximo;
        this.radioActual = 0;
        this.duracionTicks = duracionTicks;
        this.ticks = 0;
        this.activa = true;
    }

    public void actualizar(Gondolf gondolf) {
        if (!activa) return;

        ticks++;
        // RADIO BASE LINEAL
        double radioBase = ((double)ticks / duracionTicks) * radioMaximo;
        
        // EFECTO PULSO
        double pulso = 5 * Math.sin(ticks * 0.3);
        radioActual = radioBase + pulso;

        // DAÑO GRADUAL SI ESTA DENTRO DEL DAÑO
        double dist = Math.hypot(gondolf.getX() - x, gondolf.getY() - y);
        if (dist <= radioBase) {
            gondolf.perderVida(1); // DAÑO GRADUAL, AJUSTAR SI SE QUIERE
        }

        if (ticks >= duracionTicks) {
            activa = false;
        }
    }

    public void dibujar(Entorno entorno) {
        if (!activa) return;

        int baseAlpha = (int)(255 * (1.0 - (double)ticks / duracionTicks));
        baseAlpha = Math.max(baseAlpha, 0);

        // CIRCULO PRINCIPAL PULSANTE
        Color colorExterior = new Color(255, 100, 0, Math.min(baseAlpha + 100, 255));
        entorno.dibujarCirculo((int)x, (int)y, (int)radioActual, colorExterior);

        // CIRCULO INTERNO SUAVE
        Color colorInterior = new Color(255, 180, 80, baseAlpha);
        entorno.dibujarCirculo((int)x, (int)y, (int)(radioActual * 0.7), colorInterior);

        // EFECTO HALO PULSANTE (ONDA)
        double frecuencia = 0.15;
        double haloRadio = radioMaximo * (0.8 + 0.2 * Math.sin(ticks * frecuencia));
        int haloAlpha = (int)(baseAlpha * 0.5);
        if (haloAlpha > 0) {
            Color colorHalo = new Color(255, 200, 50, haloAlpha);
            entorno.dibujarCirculo((int)x, (int)y, (int)haloRadio, colorHalo);
        }

        // PARTICULAS ALEATORIAS ALREDEDOR (PEQUEÑOS PUNTOS)
        int numParticulas = 8;	//  CANTIDAD DE PARTICULAS
        for (int i = 0; i < numParticulas; i++) {
            double angulo = 2 * Math.PI * i / numParticulas + ticks * 0.1;
            double radioParticula = radioActual * 0.9 + 5 * Math.sin(ticks * 0.3 + i);
            int px = (int)(x + radioParticula * Math.cos(angulo));
            int py = (int)(y + radioParticula * Math.sin(angulo));
            
            //  COLOR DE LAS PARTICULAS
            Color colorParticula = new Color(255, 220, 150, (int)(baseAlpha * 0.7));
            entorno.dibujarCirculo(px, py, 3, colorParticula);
        }
    }


    public boolean estaActiva() {
        return activa;
    }
}
