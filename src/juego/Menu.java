package juego;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;

import entorno.Entorno;

public class Menu {

    private Hechizo hechizoSeleccionado;

    public Menu() {
        hechizoSeleccionado = null;
    }

    public void seleccionarHechizo(int mx, int my, ArrayList<Hechizo> hechizos, int magiaActual) {
        // Coordenadas para la LÓGICA de selección de hechizos
        int menuX = Juego.WIDTH_JUEGO; // Inicio del panel de menú
        int menuAncho = Juego.WIDTH_TOTAL - Juego.WIDTH_JUEGO; // Ancho del panel de menú (200)

        int botonAncho = 115; // Un ancho razonable para el botón
        int botonAltura = 40;
        // Centramos el botón en el panel del menú
        int botonX = menuX + (menuAncho - botonAncho) / 2; // X de la esquina superior izquierda del botón
        int botonYBase = 310; // Donde empieza el primer botón de hechizo (igual que en dibujar)
        int espaciadoEntreBotones = 124; // Espacio vertical entre botones

        for (int i = 0; i < hechizos.size(); i++) {
            Hechizo h = hechizos.get(i);
            // Coordenadas Y del botón actual
            int yBoton = botonYBase + i * (botonAltura + espaciadoEntreBotones);

            // Crear un Rectangle temporal para la detección del clic
            Rectangle areaBoton = new Rectangle(botonX, yBoton, botonAncho, botonAltura);

            if (areaBoton.contains(mx, my)) {
                System.out.println("Clic detectado en área de: " + h.getNombre()); // Para depurar
                if (magiaActual >= h.getCostoMagia()) {
                    hechizoSeleccionado = h;
                    System.out.println(h.getNombre() + " seleccionado."); // Para depurar
                } else {
                    System.out.println("No hay suficiente magia para " + h.getNombre()); // Para depurar
                }
                break; // Salir del bucle una vez que se ha hecho clic en un botón
            }
        }
    }


    public Hechizo getHechizoSeleccionado() {
        return hechizoSeleccionado;
    }

    public void deseleccionar() {
        hechizoSeleccionado = null;
    }

    public void dibujar(Entorno entorno, Image fondomenu, ArrayList<Hechizo> hechizos, Hechizo seleccionado, int vida, int magia, int eliminados) {
        int menuX = Juego.WIDTH_JUEGO;
        int menuAncho = Juego.WIDTH_TOTAL - Juego.WIDTH_JUEGO;
        int menuAlto = Juego.HEIGHT;

     // ******************* ULTIMO AGREGADO **********************
        // DIBUJAMOS LA IMAGEN DE FONDO MENU 
        if (fondomenu != null) {
        	double centroXMenu = menuX + menuAncho / 2.0;
        	double centroYMenu = menuAlto / 2.0;
        	double escala = (double) menuAlto / fondomenu.getHeight(null);
        	entorno.dibujarImagen(fondomenu, centroXMenu, centroYMenu, 0, escala);
        }
        

        // DIBUJAR TEXTO Y BOTONES
        entorno.cambiarFont(null, 22, Color.BLACK);
        entorno.escribirTexto("Vida: " + vida, menuX + 50, 90);
        entorno.escribirTexto("Magia: " + magia, menuX + 50, 150);
        entorno.escribirTexto("Eliminados: " + eliminados, menuX + 30, 210);

        
        int barraX = menuX + 50;
        int barraY = 70;
        int barraAncho = 100;
        int barraAlto = 10;
        
        // BARRA VIDA Y MANA
        entorno.dibujarRectangulo(barraX + barraAncho/2, barraY + 30, barraAncho, barraAlto, 0, Color.DARK_GRAY);
        int anchoVida = (int)((vida / 100.0) * barraAncho);
        entorno.dibujarRectangulo(barraX + anchoVida / 2, barraY + 30, anchoVida, barraAlto, 0, Color.RED);

        
        int barraYMagia = barraY + 15;
        entorno.dibujarRectangulo(barraX + barraAncho/2, barraYMagia + 80, barraAncho, barraAlto, 0, Color.DARK_GRAY);
        int anchoMagia = (int)((magia / 100.0) * barraAncho);
        entorno.dibujarRectangulo(barraX + anchoMagia / 2, barraYMagia + 80, anchoMagia, barraAlto, 0, Color.BLUE);

        int botonX = menuX + 50;
        int botonYBase = 325;
        int botonAltura = 30;
        int botonAncho = 100;

        for (int i = 0; i < hechizos.size(); i++) {
            Hechizo h = hechizos.get(i);
            int yBoton = botonYBase + i * (botonAltura + 120);
            boolean disponible = magia >= h.getCostoMagia();

            Color colorBoton;
            if (!disponible) {
            	colorBoton = new Color(180, 150, 120); 
            } else if (h == seleccionado) {
            	colorBoton = new Color(180, 150, 120); 
            } else {
                colorBoton = Color.LIGHT_GRAY;
            }
            
         // ******************* ULTIMO AGREGADO **********************
            // CORRECCION DE LOS BOTONES 
            entorno.dibujarRectangulo(botonX + botonAncho / 2, yBoton + botonAltura / 2, botonAncho, botonAltura, 0, colorBoton);
            entorno.cambiarFont(null, 14, disponible ? Color.BLACK : Color.DARK_GRAY);
            entorno.escribirTexto(h.getNombre() + " (M:" + h.getCostoMagia() + ")", botonX + 5, yBoton + 20);

            if (!disponible) {
                entorno.cambiarFont(null, 12, Color.RED);
                entorno.escribirTexto("No magia", botonX + 15, yBoton + 30);
            }

        }
    }
}
