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
        
    	// COORDENADAS PARA LA LÓGICA DE SELECCION DE HECHIZOS
        int menuX = Juego.WIDTH_JUEGO; 							// INICIO DEL PANEL MENU
        int menuAncho = Juego.WIDTH_TOTAL - Juego.WIDTH_JUEGO; // ANCHO DEL PANEL MENU (200)

        int botonAncho = 115; 	// ANCHO DEL BOTON
        int botonAltura = 40;
        
        // CENTRAMOS EL BOTON EN EL PANEL DEL MENU
        int botonX = menuX + (menuAncho - botonAncho) / 2; // X DE LA ESQUINA SUPERIOR IZQUIERDA DEL BOTON
        int botonYBase = 310; // DONDE EMPIEZA EL PRIMER BOTON DE HECHIZO
        int espaciadoEntreBotones = 124; // ESPACIO VERTICAL ENTRE BOTONES
        
        for (int i = 0; i < hechizos.size(); i++) {
            Hechizo h = hechizos.get(i);
            
            // COORDENADAS Y DEL BOTON ACTUAL
            
            int yBoton = botonYBase + i * (botonAltura + espaciadoEntreBotones);

            // CREA UN RECTANGULO TEMPORAL PARA LA DETECCION DEL CLICK
            Rectangle areaBoton = new Rectangle(botonX, yBoton, botonAncho, botonAltura);

            if (areaBoton.contains(mx, my)) {
                System.out.println("Clic detectado en área de: " + h.getNombre()); // DEPURA LA SELECCION
                if (magiaActual >= h.getCostoMagia()) {
                    hechizoSeleccionado = h;
                    System.out.println(h.getNombre() + " seleccionado."); // DEPURA LA SELECCION
                } else {
                    System.out.println("No hay suficiente magia para " + h.getNombre()); // DEPURA LA SELECCION
                }
                break; // SALE DEL BUCLE UNA VEZ QUE SE HA HECHO CLICK EN UN BOTON
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
        
        // DIBUJA LA IMAGEN DE FONDO MENU 
        if (fondomenu != null) {
        	double centroXMenu = menuX + menuAncho / 2.0;
        	double centroYMenu = menuAlto / 2.0;
        	double escala = (double) menuAlto / fondomenu.getHeight(null);
        	entorno.dibujarImagen(fondomenu, centroXMenu, centroYMenu, 0, escala);
        }
        

        // DIBUJA TEXTO Y BOTONES
        entorno.cambiarFont("Century", 20, Color.BLACK);	// TIPO DE LETRA, TAMAÑO, COLOR
        entorno.escribirTexto("Vida: " + vida, menuX + 50, 90);	// TEXTO, UBCACION EN X, UBICACION EN Y
        entorno.escribirTexto("Magia: " + magia, menuX + 50, 150);	// TEXTO, UBCACION EN X, UBICACION EN Y
        entorno.escribirTexto("Eliminados: " + eliminados, menuX + 40, 210);	// TEXTO, UBCACION EN X, UBICACION EN Y

        
        int barraX = menuX + 50;
        int barraY = 70;
        int barraAncho = 100;
        int barraAlto = 10;
        
        // BARRA VIDA Y MAGIA
        entorno.dibujarRectangulo(barraX + barraAncho/2, barraY + 30, barraAncho, barraAlto, 0, Color.DARK_GRAY);
        int anchoVida = (int)((vida / 100.0) * barraAncho);
        entorno.dibujarRectangulo(barraX + anchoVida / 2, barraY + 30, anchoVida, barraAlto, 0, Color.RED);

        
        int barraYMagia = barraY + 15;
        entorno.dibujarRectangulo(barraX + barraAncho/2, barraYMagia + 80, barraAncho, barraAlto, 0, Color.DARK_GRAY);
        int anchoMagia = (int)((magia / 100.0) * barraAncho);
        entorno.dibujarRectangulo(barraX + anchoMagia / 2, barraYMagia + 80, anchoMagia, barraAlto, 0, Color.BLUE);

        int botonX = menuX + 50;
        int botonYBase = 325;
        int botonAltura = 40;
        int botonAncho = 100;

        for (int i = 0; i < hechizos.size(); i++) {
            Hechizo h = hechizos.get(i);
            int yBoton = botonYBase + i * (botonAltura + 120);
            boolean disponible = magia >= h.getCostoMagia();

            
            Color colorBoton;
            if (!disponible) {
            	colorBoton = new Color(215, 0, 0);	//  COLOR EN FORMATO RGB, CUANDO NO ESTA DISPONIBLE
            } else if (h == seleccionado) {
            	colorBoton = new Color(144, 238, 144);	//  CUANDO ESTA SELECCIONADO
            } else {
                colorBoton = Color.LIGHT_GRAY;		//  SI ESTA EN MODO ESPERA
            }
            
         // ******************* ULTIMO AGREGADO **********************
            
            // CORRECCION DE LOS BOTONES 
            entorno.dibujarRectangulo(botonX + botonAncho / 2, yBoton + botonAltura / 2, botonAncho, botonAltura, 0, colorBoton);
            entorno.cambiarFont(null, 14, disponible ? Color.BLACK : Color.DARK_GRAY);
            entorno.escribirTexto(h.getNombre(), botonX + 30, yBoton + 20);

            if (!disponible) {
                entorno.cambiarFont(null, 10, Color.WHITE);
                entorno.escribirTexto("NO MAGIA", botonX + 22, yBoton + 32);	//  APARECE EL TEXTO CUANDO LOS BOTONES NO ESTAN DISP.
            }

        }
    }
}
