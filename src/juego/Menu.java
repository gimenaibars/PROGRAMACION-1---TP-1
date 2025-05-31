package juego;

import java.awt.Color;
import java.util.ArrayList;

import entorno.Entorno;

public class Menu {

    private Hechizo hechizoSeleccionado;

    public Menu() {
        hechizoSeleccionado = null;
    }

    public void seleccionarHechizo(int mx, int my, ArrayList<Hechizo> hechizos, int magiaActual) {
        int botonX = Juego.WIDTH_JUEGO + 50;
        int botonYBase = 110;
        int botonAltura = 40;
        int botonAncho = 100;

        for (Hechizo h : hechizos) {
            int index = hechizos.indexOf(h);
            int yBoton = botonYBase + index * (botonAltura + 10);

            if (mx >= botonX && mx <= botonX + botonAncho && my >= yBoton && my <= yBoton + botonAltura) {
                // Verificar si tiene suficiente magia
                if (magiaActual >= h.getCostoMagia()) {
                    hechizoSeleccionado = h;
                }
                break;
            }
        }
    }


    public Hechizo getHechizoSeleccionado() {
        return hechizoSeleccionado;
    }

    public void deseleccionar() {
        hechizoSeleccionado = null;
    }

    public void dibujar(Entorno entorno, ArrayList<Hechizo> hechizos, Hechizo seleccionado, int vida, int magia, int eliminados) {
        int menuX = Juego.WIDTH_JUEGO;
        int menuAncho = Juego.WIDTH_TOTAL - Juego.WIDTH_JUEGO;
        int menuAlto = Juego.HEIGHT;

        entorno.dibujarRectangulo(menuX + menuAncho / 2, menuAlto / 2, menuAncho, menuAlto, 0, new Color(226, 196, 172));

        entorno.cambiarFont(null, 16, Color.BLACK);
        entorno.escribirTexto("Vida: " + vida, menuX + 50, 20);
        entorno.escribirTexto("Magia: " + magia, menuX + 50, 40);
        entorno.escribirTexto("Eliminados: " + eliminados, menuX + 50, 60);

        int barraX = menuX + 50;
        int barraY = 70;
        int barraAncho = 100;
        int barraAlto = 10;

        entorno.dibujarRectangulo(barraX + barraAncho/2, barraY + 5, barraAncho, barraAlto, 0, Color.DARK_GRAY);
        int anchoVida = (int)((vida / 100.0) * barraAncho);
        entorno.dibujarRectangulo(barraX + anchoVida / 2, barraY + 5, anchoVida, barraAlto, 0, Color.RED);

        int barraYMagia = barraY + 15;
        entorno.dibujarRectangulo(barraX + barraAncho/2, barraYMagia + 5, barraAncho, barraAlto, 0, Color.DARK_GRAY);
        int anchoMagia = (int)((magia / 100.0) * barraAncho);
        entorno.dibujarRectangulo(barraX + anchoMagia / 2, barraYMagia + 5, anchoMagia, barraAlto, 0, Color.BLUE);

        int botonX = menuX + 50;
        int botonYBase = 110;
        int botonAltura = 40;
        int botonAncho = 100;

        for (int i = 0; i < hechizos.size(); i++) {
            Hechizo h = hechizos.get(i);
            int yBoton = botonYBase + i * (botonAltura + 10);
            boolean disponible = magia >= h.getCostoMagia();

            Color colorBoton;
            if (!disponible) {
                colorBoton = Color.GRAY;
            } else if (h == seleccionado) {
                colorBoton = Color.GREEN;
            } else {
                colorBoton = Color.LIGHT_GRAY;
            }

            entorno.dibujarRectangulo(botonX + botonAncho / 2, yBoton + botonAltura / 2, botonAncho, botonAltura, 10, colorBoton);
            entorno.cambiarFont(null, 14, disponible ? Color.BLACK : Color.DARK_GRAY);
            entorno.escribirTexto(h.getNombre() + " (M:" + h.getCostoMagia() + ")", botonX + 5, yBoton + 25);

            if (!disponible) {
                entorno.cambiarFont(null, 12, Color.RED);
                entorno.escribirTexto("No magia", botonX + 15, yBoton + 38);
            }

        }
    }
}
