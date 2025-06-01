package juego;

import java.awt.Color;
import java.awt.Image;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import entorno.Entorno;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	
	// Imagenes del juego
	private Image fondo; // Defino la imagen para el fondo principal del área de juego
	private Image fondomenu; // Defino la imagen para el fondo del panel del menú lateral

	// Constantes de Dimensiones
	public static final int WIDTH_JUEGO = 600; // Defino el ancho del área principal de juego
	public static final int HEIGHT = 600; // Defino el alto total de la ventana del juego
	public static final int WIDTH_TOTAL = 800; // Defino el ancho total de la ventana (juego + menú)

	// Componentes del Entorno y del Juego
	private Entorno entorno; // Defino el objeto Entorno que maneja la ventana y la interacción
	private Gondolf gondolf; // Defino el objeto para mi personaje principal, Gondolf
	private ArrayList<Roca> rocas; // Defino una lista para almacenar los obstáculos (rocas)
	private ArrayList<Murcielago> murcielagos; // Defino una lista para almacenar los enemigos (murciélagos)
	private ArrayList<Hechizo> hechizos; // Defino una lista para almacenar los hechizos disponibles
	private Menu menu; // Defino el objeto que representa el menú lateral

	// Variables de Estado del juego
	private int enemigosEliminados; // Contador para los enemigos que elimino
	private int enemigosGenerados = 0; // Contador para los enemigos que ya generé
	private final int MAX_ENEMIGOS_VIVOS = 5; // Defino el número máximo de enemigos en pantalla a la vez
	private final int TOTAL_ENEMIGOS = 25; // Defino el número total de enemigos a eliminar para ganar

	// Estados del juego
	private enum GameState { // Defino los posibles estados por los que puede pasar mi juego
		MENU, // Estado para cuando muestro el menú principal/inicio
		JUGANDO, // Estado para cuando el jugador está activamente jugando
		FIN_JUEGO // Estado para cuando el juego ha terminado (ganado o perdido)
	}

	private GameState estadoActual = GameState.MENU; // Establezco el estado inicial del juego en MENU
	private String mensajeFinJuego = ""; // Almaceno el mensaje que mostraré al final del juego
	private int ticksDesdeFin = 0; // Contador de tiempo (en ticks) desde que el juego terminó

	// Defino estas constantes para manejar fácilmente la posición, tamaño y texto del botón de inicio.
	private final int START_BUTTON_WIDTH = 250; // Ancho del botón.
	private final int START_BUTTON_HEIGHT = 50; // Alto del botón.
	private final int START_BUTTON_X = WIDTH_JUEGO / 2 - START_BUTTON_WIDTH / 2; // Defino la posicion Y del botón.
	private final int START_BUTTON_Y = HEIGHT - 100 - 20 - 20;  // heigth - 140. 
 	private final String START_BUTTON_TEXT = "INICIAR JUEGO"; // Texto que mostrará el botón.


	private Random rand = new Random(); // Creo una instancia de Random para generar números aleatorios

	
	// CONSTRUCCTOR DE LA CLASE JUEGO 
	public Juego() {
		// Inicializo el Entorno, estableciendo el título y las dimensiones de la ventana
		this.entorno = new Entorno(this, "El camino de Gondolf - Grupo 13", WIDTH_TOTAL, HEIGHT);
		
		// Cargo las imágenes del juego desde la carpeta "imagenes"
		this.fondo = new ImageIcon(getClass().getResource("/imagenes/fondo.png")).getImage();
		this.fondomenu = new ImageIcon(getClass().getResource("/imagenes/fondomenu.png")).getImage();

		// Creo e inicializo mi personaje Gondolf en una posición inicial
		this.gondolf = new Gondolf(300, 300);

		// Inicializo la lista de rocas y agrego algunas rocas fijas al escenario
		this.rocas = new ArrayList<>();
		rocas.add(new Roca(150, 150));
		rocas.add(new Roca(400, 250));
		rocas.add(new Roca(300, 500));
		rocas.add(new Roca(100, 400));
		rocas.add(new Roca(500, 100));

		// Inicializo la lista de murciélagos (inicialmente vacía, se generan durante el juego)
		this.murcielagos = new ArrayList<>();
		this.murcielagos = new ArrayList<>();
		
		// Inicializo la lista de hechizos y agrego los hechizos disponibles
		this.hechizos = new ArrayList<>();
		hechizos.add(new Hechizo("Fuego", 100, 30, Color.black));
		hechizos.add(new Hechizo("Hielo", 80, 50, Color.black));

		// Creo el objeto para el menú lateral
		this.menu = new Menu();

		// Inicio el motor del juego (esto llama al método tick() repetidamente)
		this.entorno.iniciar();
	}

	// Creo un nuevo murciélago en una posición aleatoria fuera de la pantalla visible del juego.
	// Esto hace que los murciélagos "aparezcan" desde los bordes.
	private Murcielago crearMurcielagoFueraPantalla() {
		int lado = rand.nextInt(4); // Elijo un lado aleatorio (0:arriba, 1:derecha, 2:abajo, 3:izquierda)
		double x = 0, y = 0; // Coordenadas iniciales

		// Asigno las coordenadas según el lado elegido
		switch (lado) {
			case 0: x = rand.nextDouble() * WIDTH_JUEGO; y = -20; break; // Arriba
			case 1: x = WIDTH_JUEGO + 20; y = rand.nextDouble() * HEIGHT; break; // Derecha
			case 2: x = rand.nextDouble() * WIDTH_JUEGO; y = HEIGHT + 20; break; // Abajo
			case 3: x = -20; y = rand.nextDouble() * HEIGHT; break; // Izquierda
		}
		return new Murcielago(x, y); // Devuelvo el nuevo murciélago
	}

	// Cuento cuántos murciélagos están actualmente vivos en el juego.
	private int contarMurcielagosVivos() {
		int count = 0; // inicio mi contador
		for (Murcielago m : murcielagos) { // Recorro la lista de murcielagos
			if (m.estaVivo()) count++; // Incremento el contador
		}
		return count; // Devuelvo el total
	}

	// Verifico si un punto (x2, y2) está dentro de un rango específico desde otro punto (x1, y1).
	// Uso esto para ataques cuerpo a cuerpo o detección de clics cercanos.
	// return true si está dentro del rango, false si no.
	private boolean dentroDelRango(double x1, double y1, double x2, double y2, double rango) {
		return Math.hypot(x1 - x2, y1 - y2) <= rango;
	}

	// Actualizo el estado de todos los murciélagos.
	private void actualizarEstadoMurcielagos() {
		long tiempoActual = System.currentTimeMillis(); // Obtengo el tiempo actual para animaciones o estados temporales.

		// Actualizo cada murciélago
		for (Murcielago m : murcielagos) {
			m.actualizarEstado(tiempoActual); // Llamo al método de actualización individual del murciélago.
			if (m.estaVivo()) { // Si el murciélago sigue vivo después de su actualización.
				m.perseguir(gondolf); // Hago que persiga a Gondolf.
				m.atacarSiColisiona(gondolf); // Hago que ataque si hay colisión.
			}
		}

		murcielagos.removeIf(m -> {
			if (!m.estaVivo() && !m.estaCongelado() && !m.estaQuemado()) { // Elimino los murciélagos que ya no están vivos (y no están en estados especiales como congelado/quemado).
				enemigosEliminados++; // y actualizo el contador de enemigos eliminados.
				return true; // Indico que este murciélago debe ser eliminado de la lista.
			}
			return false; // Este murciélago no se elimina.
		});

		// Genero nuevos murciélagos si hay menos del máximo permitido en pantalla
		if (contarMurcielagosVivos() < MAX_ENEMIGOS_VIVOS && enemigosGenerados < TOTAL_ENEMIGOS) {
			murcielagos.add(crearMurcielagoFueraPantalla()); // Añado un nuevo murciélago
			enemigosGenerados++; // Incremento el contador de generados
		}
	}

	// Procesa el InputJuego
	private void procesarInputJuego() {
		// Movimiento
		double dx = 0, dy = 0;
		if (entorno.estaPresionada(entorno.TECLA_DERECHA) || entorno.estaPresionada('d')) dx = 5;
        if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA)|| entorno.estaPresionada('a')) dx = -5;
        if (entorno.estaPresionada(entorno.TECLA_ARRIBA)|| entorno.estaPresionada('w')) dy = -5;
        if (entorno.estaPresionada(entorno.TECLA_ABAJO) || entorno.estaPresionada('s')) dy = 5;

		gondolf.regenerarMagia();
		gondolf.mover(dx, dy, rocas);

		if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
			int mx = entorno.mouseX();
			int my = entorno.mouseY();

			if (mx > WIDTH_JUEGO) {
				menu.seleccionarHechizo(mx, my, hechizos, gondolf.getMagia());
			} else {
				Hechizo h = menu.getHechizoSeleccionado();
				if (h != null && gondolf.puedeUsarHechizo(h.getCostoMagia())) {
				    long tiempoActual = System.currentTimeMillis();

				    for (Murcielago m : murcielagos) {
				        if (!m.estaVivo()) continue;

				        if (h.getNombre().equals("Hielo")) {
				            m.congelar(tiempoActual);
				        } else if (h.getNombre().equals("Fuego")) {
				            m.quemar(tiempoActual);
				        } else {
				            h.lanzar((int)gondolf.getX(), (int)gondolf.getY(), murcielagos, gondolf);
				        }
				    }

				    gondolf.consumirMagia(h.getCostoMagia());
				    menu.deseleccionar();

				} else if (h == null) {
					for (Murcielago m : murcielagos) {
						if (m.estaVivo() && dentroDelRango(m.getX(), m.getY(), mx, my, 20)) {
							m.eliminarSinAnimacion();
							break;
						}
					}
				}
			}
		}
	}

	// Procesa la entrada para el menú
	private void procesarInputMenu() {
	    if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
	        int mx = entorno.mouseX();
	        int my = entorno.mouseY();

	        // Verifico si el clic se realizó dentro del área del botón "INICIAR JUEGO"
	        // Defino el área de clic del botón usando las mismas constantes que su diseño.
	        if (mx >= START_BUTTON_X && mx <= START_BUTTON_X + START_BUTTON_WIDTH &&
	            my >= START_BUTTON_Y && my <= START_BUTTON_Y + START_BUTTON_HEIGHT) {
	            estadoActual = GameState.JUGANDO; // Cambiar al estado del juego
	        }
	    }
	}

	private void dibujarFondo() {
	    int anchoOriginal = fondo.getWidth(null);
	    int altoOriginal = fondo.getHeight(null);
	    double escalaX = (double) WIDTH_JUEGO / anchoOriginal;
	    double escalaY = (double) HEIGHT / altoOriginal;
	    double escala = Math.min(escalaX, escalaY);
	    entorno.dibujarImagenConCentro(fondo, WIDTH_JUEGO / 2, HEIGHT / 2, anchoOriginal / 2.0, altoOriginal / 2.0, 0, escala);
	}

	private void dibujarJuego() {
		dibujarFondo();

		// Personajes
		gondolf.dibujar(entorno);
		for (Roca r : rocas) r.dibujar(entorno);
		for (Murcielago m : murcielagos)
			if (m.estaVivo()) m.dibujar(entorno);

		 menu.dibujar(entorno, this.fondomenu, hechizos, menu.getHechizoSeleccionado(), gondolf.getVida(), gondolf.getMagia(), enemigosEliminados);
	}

	// Dibuja el menú de inicio
	private void dibujarMenuInicio() {
	    dibujarFondo(); // Dibujar el fondo principal
	    
		// Configuro la fuente para los textos de instrucciones
	    entorno.cambiarFont("Constantia", 20, Color.WHITE);

	    // Defino posiciones y espaciado para los textos de ayuda
	    int startX = 50; // Posicion X para los textos
	    int startY = 100; // Posicion Y para los textos
	    int lineHeight = 25; // Espacios entre las lineas

	    // Escribo los textos de "Cómo jugar"
	    entorno.escribirTexto("Cómo jugar: ", startX, startY);
	    entorno.escribirTexto("Debes eliminar a todos los murciélagos para ganar.", startX + 20, startY +10 + lineHeight);
	    entorno.escribirTexto("Usa los hechizos (seleccionándolos a la derecha).", startX + 20, startY + 35 + lineHeight);
	    entorno.escribirTexto("Controla tu maná y tu vida. Si te quedas sin vida, ¡pierdes!", startX + 20, startY + 60 + lineHeight);

	    // Avanzo la posición Y para los textos de "Controles"
	    startY += 6 * lineHeight;
	    entorno.escribirTexto("Controles: ", startX, startY);
	    entorno.escribirTexto("Avanzar: ↑ o W", startX + 20, startY + lineHeight);
	    entorno.escribirTexto("Retroceder: ↓ o S", startX + 20, startY + 2 * lineHeight);
	    entorno.escribirTexto("Izquierda: ← o A", startX + 20, startY + 3 * lineHeight);
	    entorno.escribirTexto("Derecha: → o D", startX + 20, startY + 4 * lineHeight);
	    entorno.escribirTexto("Lanzar Hechizo: Click Izquierdo", startX + 20, startY + 5 * lineHeight);
	    
	    // Mensaje de ánimo
	    entorno.cambiarFont("Consolas", 30, Color.GREEN);
	    entorno.escribirTexto("¡Suerte, Hechicero!", WIDTH_JUEGO / 2 - 160, startY + 7 * lineHeight);

	    // Dibuja el boton iniciar juego
	    entorno.cambiarFont("Consolas", 20, Color.GREEN);
	    
	    // Dibuja el rectangulo del boton
	    entorno.dibujarRectangulo(START_BUTTON_X + START_BUTTON_WIDTH / 2, START_BUTTON_Y + START_BUTTON_HEIGHT / 2, START_BUTTON_WIDTH, START_BUTTON_HEIGHT, 0, Color.GREEN);
	    
	    // Configuro la fuente para el texto del botón
	    entorno.cambiarFont("Arial", 26, Color.WHITE);
	    
	    // Centrado del texto dentro del botón
	    double offsetX = 0; 
	    double offsetY = 0;
	    
	    // Ancho y altura del texto según el tamaño y la longitud de la fuente de "INICIAR JUEGO"
	    double estimatedTextWidth = 180; 
	    double estimatedTextHeight = 34; 

	    // Para centrar verticalmente, considero la altura de la fuente.
	    // (START_BUTTON_HEIGHT / 2) me da el centro. Luego subo la mitad de la altura de la fuente (aprox)
	    entorno.escribirTexto(START_BUTTON_TEXT,
	                         START_BUTTON_X + (START_BUTTON_WIDTH - estimatedTextWidth) / 2 + offsetX,
	                         START_BUTTON_Y + (START_BUTTON_HEIGHT - estimatedTextHeight) / 2 + estimatedTextHeight * 0.75 + offsetY); // The 0.75 is a common vertical adjustment for baseline

	    // Paso la imagen de fondo del menú a la clase Menú
	    menu.dibujar(entorno, this.fondomenu, hechizos, null, gondolf.getVida(), gondolf.getMagia(), enemigosEliminados);
	}

	// Dibujo la pantalla de fin del juego
	private void dibujarFinJuego() {
	    dibujarJuego(); // Dibuja el estado final del juego.
	    entorno.cambiarFont("Constantia", 35, Color.green); // Fuente para el mensaje de fin
	    entorno.escribirTexto(mensajeFinJuego, WIDTH_JUEGO / 2 - 250, HEIGHT / 2); // Calculo la posición X para centrar el mensajeFinJuego. 
	}

	// Verifico las condiciones para terminar el juego (Gondolf sin vida o todos los enemigos eliminados).
	// Si se cumple alguna, cambio el estado a FIN_JUEGO
	private void verificarFinJuego() {
		if (!gondolf.estaVivo()) { // Si Gondolf ya no esta vivo
			mensajeFinJuego = "¡Has perdido! Gondolf murió. :("; // Muestro mensaje Perdió
			estadoActual = GameState.FIN_JUEGO; // Cambio el estado a fin de juego
			ticksDesdeFin = 0; // Reinicio el contador de tiempo 
		} else if (enemigosEliminados >= TOTAL_ENEMIGOS) { // Si eliminé a todos los murcielagos
			mensajeFinJuego = "¡Has ganado! Gondolf sobrevivió."; // Muestro mensaje Ganó
			estadoActual = GameState.FIN_JUEGO; // Cambio el estado a fin de juego
			ticksDesdeFin = 0; // Reinicio el contador de tiempo
		}
	}

	// Método principal del bucle del juego, llamado por Entorno en cada "tick".
	// Gestiona la lógica y el dibujo según el estado actual del juego.
	public void tick() {
		switch (estadoActual) { // Evalúo el estado actual del juego
			case MENU: // Si estoy en el menú principal
				dibujarMenuInicio(); // Dibujo la pantalla de inicio
				procesarInputMenu(); // Proceso la entrada del usuario para el menú
				break;

			case JUGANDO: // Si estoy jugando activamente
				procesarInputJuego(); // Proceso la entrada del usuario para el juego
				actualizarEstadoMurcielagos(); // Actualizo el estado de los enemigos
				dibujarJuego(); // Dibujo todos los elementos del juego
				verificarFinJuego(); // Verifico si el juego ha terminado
				break;

			case FIN_JUEGO: // Si el juego ha terminado
				dibujarFinJuego(); // Dibujo la pantalla de fin de juego
				ticksDesdeFin++; // Incremento el contador de tiempo
				if (ticksDesdeFin > 300) { // Esperar 5 segundos (60 ticks/seg * 5 s = 300)
					System.exit(0); // Cierro la aplicacion
				}
				break;
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Juego juego = new Juego();
	}
}