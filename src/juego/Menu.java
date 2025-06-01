package juego;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle; 
import java.util.ArrayList;

import entorno.Entorno;

public class Menu {

    private Hechizo hechizoSeleccionado; // Almacena el hechizo que el jugador  ha clickeado

    public Menu() {
        hechizoSeleccionado = null; // Inicialmente, no hay ningún hechizo seleccionado
    }
    
    
    //  Verifica si el clic del mouse (mx, my) cae sobre alguno de los botones de hechizo
    //  y actualiza el hechizoSeleccionado si hay suficiente magia. 
    public void seleccionarHechizo(int mx, int my, ArrayList<Hechizo> hechizos, int magiaActual) {
        
    	// Coordenadas para la LÓGICA de selección de hechizos ( area y dimensiones )
        
    	int menuX = Juego.WIDTH_JUEGO; // Coordenada X donde comienza el panel del menú
        int menuAncho = Juego.WIDTH_TOTAL - Juego.WIDTH_JUEGO; // Ancho total del panel del menú (200px)

        int botonAncho = 115; //  Ancho de cada botón
        int botonAltura = 40; //  Altura de cada botón
        
        int botonX = menuX + (menuAncho - botonAncho) / 2; // Calcula la coordenada X para que los botones queden centrados dentro del panel del menú
        int botonYBase = 310; // Coordenada Y donde se comenzará a dibujar el primer botón de hechizo
        int espaciadoEntreBotones = 124; // Espacio vertical total entre el inicio de un botón y el inicio del siguiente

        // Itera sobre cada hechizo para verificar si el clic cayó sobre su botón
        for (int i = 0; i < hechizos.size(); i++) {  // Calcula la coordenada Y del borde superior del botón actual
            Hechizo h = hechizos.get(i);
            // Coordenadas Y del botón actual
            int yBoton = botonYBase + i * (botonAltura + espaciadoEntreBotones);

            // Crear un Rectangle temporal para la detección del clic
            Rectangle areaBoton = new Rectangle(botonX, yBoton, botonAncho, botonAltura);

            // Verificar si el punto (mx, my) está contenido dentro del área del botón
            if (areaBoton.contains(mx, my)) {
                System.out.println("Clic detectado en área de: " + h.getNombre()); // Imprime en consola si se detecta un clic en el área
                
                // Verificar si el jugador tiene suficiente magia para seleccionar este hechizo 
                if (magiaActual >= h.getCostoMagia()) {
                    hechizoSeleccionado = h;  // Asigna el hechizo como seleccionado
                    System.out.println(h.getNombre() + " seleccionado."); // Confirma que el hechizo fue seleccionado
                } else {
                    System.out.println("No hay suficiente magia para " + h.getNombre()); // Informa si no hay magia suficiente
                }
                break; // Salir del bucle una vez que se ha encontrado y procesado un clic en un botón
            }
        }
    }


    // Devuelve el hechizo que está actualmente seleccionado, o null si no hay ninguno.
    public Hechizo getHechizoSeleccionado() {
        return hechizoSeleccionado;
    }

    // Anula la seleccion actual del hechizo.
    public void deseleccionar() {
        hechizoSeleccionado = null;
    }

    // Dibuja todos los elementos del menú en la pantalla.
    public void dibujar(Entorno entorno, Image fondomenu, ArrayList<Hechizo> hechizos, Hechizo seleccionado, int vida, int magia, int eliminados) {
        
    	// Definimos las dimensiones y posición base del panel del menú
    	int menuX = Juego.WIDTH_JUEGO; // Coordenada X donde el panel del menú comienza
        int menuAncho = Juego.WIDTH_TOTAL - Juego.WIDTH_JUEGO; // Ancho del panel del menú
        int menuAlto = Juego.HEIGHT; // Alto del panel del menú (ocupa toda la altura del juego)

        // DIBUJAMOS LA IMAGEN DE FONDO MENU 
        if (fondomenu != null) {
        	double centroXMenu = menuX + menuAncho / 2.0; // Centro horizontal del panel del menú
        	double centroYMenu = menuAlto / 2.0; // Centro vertical del panel del menú
        	double escala = (double) menuAlto / fondomenu.getHeight(null); // Calcula la escala para que la imagen de fondo cubra el alto del menú manteniendo su proporción
        	entorno.dibujarImagen(fondomenu, centroXMenu, centroYMenu, 0, escala); // Para cubrir completamente y posiblemente recortar, se usaría Math.max de escalaX y escalaY
        }
        

        // Dibujar textos de información (Vida, Magia, Eliminados)
        entorno.cambiarFont(null, 22, Color.BLACK);  // Establece fuente y color para los textos
        entorno.escribirTexto("Vida: " + vida, menuX + 50, 90);
        entorno.escribirTexto("Magia: " + magia, menuX + 50, 150);
        entorno.escribirTexto("Eliminados: " + eliminados, menuX + 30, 210);

        // Definición de las barras de Vida y Maná
        int barraX = menuX + 50; // Coordenada X de inicio para las barras
        int barraY = 70; // Esta variable no se usa directamente para la posición Y de las barras de Vida/Maná
        int barraAncho = 100; // Ancho de las barras
        int barraAlto = 10; // Alto de las barras
        
        // Barra de vida
        entorno.dibujarRectangulo(barraX + barraAncho/2, barraY + 30, barraAncho, barraAlto, 0, Color.DARK_GRAY); // Fondo de la barra de vida (gris oscuro) // Y=100
        int anchoVida = (int)((vida / 100.0) * barraAncho);  // Calcula el ancho proporcional a la vida
        entorno.dibujarRectangulo(barraX + anchoVida / 2, barraY + 30, anchoVida, barraAlto, 0, Color.RED);  // Parte coloreada de la barra de vida (rojo) // Y=100

        // Barra de Maná
        entorno.dibujarRectangulo(barraX + barraAncho / 2.0, (70 + 15) + 80, barraAncho, barraAlto, 0, Color.DARK_GRAY); // Fondo de la barra de maná (gris oscuro) // Y = 85 + 80 = 165
        int anchoMagia = (int) ((magia / 100.0) * barraAncho); // Calcula el ancho proporcional a la magia
        entorno.dibujarRectangulo(barraX + anchoMagia / 2.0, (70 + 15) + 80, anchoMagia, barraAlto, 0, Color.BLUE); // Parte coloreada de la barra de maná (azul) // Y = 165 

        // Definición de los botones de hechizos (para el dibujo)
        // Dimensiones y posiciones coincidan para el uso en seleccionarHechizo()
        // para que el área al clickear corresponda al botón visual.
        int botonX = menuX + 50;
        int botonYBase = 325;
        int botonAltura = 30;
        int botonAncho = 100;

        // Itera y dibuja cada botón de hechizo
        for (int i = 0; i < hechizos.size(); i++) {
            Hechizo h = hechizos.get(i);
            int yBoton = botonYBase + i * (botonAltura + 120); // Calcula la coordenada Y del borde superior del botón visual actual
            boolean disponible = magia >= h.getCostoMagia(); // Verifica si hay magia para usar el hechizo
            
            Color colorBoton; // Determina el color del botón según su estado 
            if (!disponible) { // Si no hay suficiente magia
            	colorBoton = new Color(120, 150, 120); // Un color para indicar no disponible (verde agua, super clarito)
            } else if (h == seleccionado) { // Si el hechizo está seleccionado
            	colorBoton = new Color(100, 220, 100); // Un color para indicar que esta clickeado (verde brillante)
            } else { // Hechizo disponible pero no seleccionado
                colorBoton = Color.LIGHT_GRAY; // Color estándar para disponible
            }
            
            // Dibujar el rectángulo del botón
            entorno.dibujarRectangulo(botonX + botonAncho / 2, yBoton + botonAltura / 2, botonAncho, botonAltura, 0, colorBoton);
            entorno.cambiarFont(null, 14, disponible ? Color.BLACK : Color.DARK_GRAY); // Color del texto según disponibilidad
            entorno.escribirTexto(h.getNombre() + " (M:" + h.getCostoMagia() + ")", botonX + 5, yBoton + 20); // Ajusta X e Y para centrar el texto

            // Si no está disponible, muestra un mensaje de "No magia"
            if (!disponible) {
                entorno.cambiarFont(null, 12, Color.RED); // Letra roja para el aviso
                entorno.escribirTexto("No magia", botonX + 15, yBoton + 30); // Posiciona el aviso
            }

        }
    }
}
