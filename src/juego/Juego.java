package juego;

import java.awt.Color;
import java.awt.Image;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import entorno.Entorno;
import entorno.Herramientas;
import entorno.InterfaceJuego;


public class Juego extends InterfaceJuego {
	private Image fondo;
	private Image fondomenu;

	public static final int WIDTH_JUEGO = 600;
	public static final int HEIGHT = 600;
	public static final int WIDTH_TOTAL = 800;

	private Entorno entorno;
	private Gondolf gondolf;
	private ArrayList<Roca> rocas;
	private ArrayList<Murcielago> murcielagos;
	private ArrayList<Hechizo> hechizos;
	private ArrayList<AreaHechizo> areasHechizo = new ArrayList<>(); 
	private ArrayList<EfectoVisual> efectos = new ArrayList<>(); 
	private Nivel[] niveles;
	private int nivelActualIndex;
	private Nivel nivelActual;
	private Menu menu;
	private boolean mostrarTransicionNivel = true;
	private int ticksTransicionNivel = 0;
	private final int duracionMensajeNivel = 120; 

	private int enemigosEliminados;
	private int enemigosGenerados = 0;
	private final int MAX_ENEMIGOS_VIVOS = 5; //enemigos vivos en pantalla
	private final int TOTAL_ENEMIGOS = 25; //cant enemigos a matar
	
	
	private boolean juegoTerminado = false;
	private String mensajeFinJuego = "";
	private int ticksDesdeFin = 0;
	private boolean mostrarMensajeInicio = true;
	private int ticksInicio = 0;

	private Random rand = new Random();

	public Juego() {
		//CONSTRUCTOR
		this.entorno = new Entorno(this, "El camino de Gondolf - Grupo 13 - BETHGE-IBARS-RODRIGUEZ", WIDTH_TOTAL, HEIGHT);
		this.fondo = new ImageIcon(getClass().getResource("/imagenes/fondo.png")).getImage();
		this.fondomenu = new ImageIcon(getClass().getResource("/imagenes/fondomenu.png")).getImage();
		
		this.gondolf = new Gondolf(300, 300);
		Herramientas.loop("sonido/musicadefondo.wav"); // Musica de fondo (todo momento)
		
		//inicio niveles
		niveles = new Nivel[] {
			    new Nivel(1, 5, 1.0),  // Nivel 1: matar 5 murciélagos, velocidad normal
			    new Nivel(2, 10, 1.3),  // Nivel 2: matar 8 murciélagos, más rápido
			    new Nivel(3, 20, 1.6)  // Nivel 3: matar 12 murciélagos, aún más rápido
			    
			};
			nivelActualIndex = 0;
			nivelActual = niveles[nivelActualIndex];


		this.rocas = new ArrayList<>();
		rocas.add(new Roca(150, 150));
		rocas.add(new Roca(400, 250));
		rocas.add(new Roca(300, 500));
		rocas.add(new Roca(100, 400));
		rocas.add(new Roca(500, 100));
		
		this.murcielagos = new ArrayList<>();
		//HECHIZOS
		this.hechizos = new ArrayList<>();
		hechizos.add(new Hechizo("Fuego", 120, 30, new Color(242, 62, 26, 20))); //rojo - rango del hechizo 120
		hechizos.add(new Hechizo("Hielo", 120, 20, new Color(36, 108, 236, 20))); //azul - rango del hechizo 120	
		///// AREA DE EFECTO DE LOS HECHIZOS
		this.menu = new Menu();

		this.entorno.iniciar();
	}

	private Murcielago crearMurcielagoFueraPantalla() {
		int lado = rand.nextInt(4);
		double x = 0, y = 0;

		switch (lado) {
			case 0: x = rand.nextDouble() * WIDTH_JUEGO; y = -20; break;
			case 1: x = WIDTH_JUEGO + 20; y = rand.nextDouble() * HEIGHT; break;
			case 2: x = rand.nextDouble() * WIDTH_JUEGO; y = HEIGHT + 20; break;
			case 3: x = -20; y = rand.nextDouble() * HEIGHT; break;
		}
		return new Murcielago(x, y);
	}
	
	
	//****************************CLASE AUXILIAR AreaHechizo ***************************************++
	class AreaHechizo {
		int x, y, radio;
		Color color;
		int ticksRestantes;

		public AreaHechizo(int x, int y, int radio, Color color, int duracionTicks) {
			this.x = x;
			this.y = y;
			this.radio = radio;
			this.color = color;
			this.ticksRestantes = duracionTicks;
		}
		

		public void dibujar(Entorno entorno) {
		    float proporcion = (float) ticksRestantes / 30f; // si dura 30 ticks, va de 1.0 a 0.0
		    int alpha = (int)(color.getAlpha() * proporcion);

		    // Nos aseguramos de que no sea menor a 0
		    alpha = Math.max(0, alpha);

		    Color colorFade = new Color(
		        color.getRed(), 
		        color.getGreen(), 
		        color.getBlue(), 
		        alpha
		    );

		    entorno.dibujarCirculo(x, y, radio * 2, colorFade);
		    ticksRestantes--;
		}


		public boolean estaActivo() {
			return ticksRestantes > 0;
		}
	}


	private int contarMurcielagosVivos() {
		int count = 0;
		for (Murcielago m : murcielagos) {
			if (m.estaVivo()) count++;
		}
		return count;
	}

	private boolean dentroDelRango(double x1, double y1, double x2, double y2, double rango) {
		return Math.hypot(x1 - x2, y1 - y2) <= rango;
	}

	private void actualizarEstadoMurcielagos() {
		long tiempoActual = System.currentTimeMillis();

		for (Murcielago m : murcielagos) {
			m.actualizarEstado(tiempoActual);
			if (m.estaVivo()) {
				m.perseguir(gondolf, nivelActual.getVelocidadMurcielagos());
				m.atacarSiColisiona(gondolf);
			}
		}

		murcielagos.removeIf(m -> {
			if (!m.estaVivo() && !m.estaCongelado() && !m.estaQuemado()) {
				if (m.fueEliminadoPorJugador()) {
					enemigosEliminados++; // Aquí sumás 1 vez por murciélago eliminado
				}
				
				return true;
			}
			return false;
		});

		if (contarMurcielagosVivos() < MAX_ENEMIGOS_VIVOS && enemigosGenerados < TOTAL_ENEMIGOS) {
			murcielagos.add(crearMurcielagoFueraPantalla());
			enemigosGenerados++;
		}
	}

	private void procesarInput() {
		// Movimiento de Gondolf
		double dx = 0, dy = 0;
		if (entorno.estaPresionada(entorno.TECLA_DERECHA) || entorno.estaPresionada('d')) dx = 5;
        if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA)|| entorno.estaPresionada('a')) dx = -5;
        if (entorno.estaPresionada(entorno.TECLA_ARRIBA)|| entorno.estaPresionada('w')) dy = -5;
        if (entorno.estaPresionada(entorno.TECLA_ABAJO) || entorno.estaPresionada('s')) dy = 5;
        ////MOVIMIENTOS CON TECLAS Y FLECHAS
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
				    

				//iniciamos el cambio de efecto magia  
				    //Dibuja el area de efecto antes de lanzar
					if (h != null && gondolf.puedeUsarHechizo(h.getCostoMagia())) {

					    int centroX = (int) gondolf.getX();
					    int centroY = (int) gondolf.getY();

					    areasHechizo.add(new AreaHechizo(centroX, centroY, h.getArea(), h.getColor(), 30));

					    boolean afecto = h.lanzar(centroX, centroY, murcielagos, gondolf);

					    if (afecto) {
					        if (h.getNombre().equals("Fuego")) {
					            Herramientas.play("sonido/musicafuego.wav"); // Sonido de fuego al estar clickeado el poder
					        } else if (h.getNombre().equals("Hielo")) {
					            Herramientas.play("sonido/musicahielo.wav"); // Sonido de hielo al estar clickeado el poder
					        }
					    }

					    gondolf.consumirMagia(h.getCostoMagia());
					    menu.deseleccionar();
					}
				

				} else if (h == null) {
					for (Murcielago m : murcielagos) {
						if (m.estaVivo() && dentroDelRango(m.getX(), m.getY(), mx, my, 20)) {
							m.eliminarSinAnimacion();
							//21:49
							m.marcarComoEliminadoPorJugador();
							//AGREGO PARA EFECTO VISUAL MURCIELAGO 
							efectos.add(new EfectoVisual((int)m.getX(), (int)m.getY(),20,15)); //tercer parametro rango del EfectoVisual
							Herramientas.play("sonido/musicaefectoaplastar.wav"); // Sonido al clickear arriba de los murcielagos para matarlos
							break;
						}
					}
				}
			}
		}
	}

	private void dibujarTodo() {
		// Fondo escalado
		int anchoOriginal = fondo.getWidth(null);
		int altoOriginal = fondo.getHeight(null);
		double escalaX = (double) WIDTH_JUEGO / anchoOriginal;
		double escalaY = (double) HEIGHT / altoOriginal;
		double escala = Math.min(escalaX, escalaY);

		entorno.dibujarImagenConCentro(fondo, WIDTH_JUEGO / 2, HEIGHT / 2, anchoOriginal / 2.0, altoOriginal / 2.0, 0, escala);
		
		//Dibujar areas de hechizo activas
		areasHechizo.removeIf(area ->!area.estaActivo());
		for (AreaHechizo area : areasHechizo) {
			area.dibujar(entorno);
			
		}
		// Personajes
		gondolf.dibujar(entorno);
		for (Roca r : rocas) r.dibujar(entorno);
		for (Murcielago m : murcielagos)
			if (m.estaVivo()) m.dibujar(entorno);

		menu.dibujar(entorno, this.fondomenu, hechizos, menu.getHechizoSeleccionado(), gondolf.getVida(), gondolf.getMagia(), enemigosEliminados);
		
		//dibujar el EfectoVisual
		efectos.removeIf(e -> !e.estaActivo());
		for (EfectoVisual ef : efectos) {
			ef.dibujar(entorno);
		}
	}

	private void verificarFinJuego() {
		if (!gondolf.estaVivo()) {
			juegoTerminado = true;
			mensajeFinJuego = "¡Has perdido! Gondolf murió.";
			ticksDesdeFin = 0;
	
			} else if (enemigosEliminados >= nivelActual.getMurcielagosObjetivo()) {
			    if (nivelActualIndex + 1 < niveles.length) {
			        nivelActualIndex++;
			        nivelActual = niveles[nivelActualIndex];
			        enemigosEliminados = 0;
			        enemigosGenerados = 0;
			        murcielagos.clear(); // Limpia murciélagos vivos
			        mostrarTransicionNivel = true;
			        ticksTransicionNivel = 0;
			    } else {
			        juegoTerminado = true;
			        mensajeFinJuego = "¡Has ganado! Todos los niveles completados.";
			        ticksDesdeFin = 0;
			    }
			}
		
	}
	

	public void tick() {
		if (mostrarMensajeInicio) {
			entorno.cambiarFont("Constantia", 20, Color.WHITE);  
			entorno.escribirTexto("INICIO DEL JUEGO", WIDTH_JUEGO / 2, HEIGHT / 2);
			ticksInicio++;
			if (ticksInicio > 180) mostrarMensajeInicio = false;
			return;
		}
		
		if (mostrarTransicionNivel) {
		    entorno.cambiarFont("Constantia", 20, Color.YELLOW);
		    String mensajeNivel = "Nivel " + nivelActual.getNumero() + " - Elimina " + nivelActual.getMurcielagosObjetivo() + " murciélagos";
		    entorno.escribirTexto(mensajeNivel, WIDTH_JUEGO / 2 - 130, HEIGHT / 2);
		    ticksTransicionNivel++;
		    if (ticksTransicionNivel > duracionMensajeNivel) {
		        mostrarTransicionNivel = false;
		    }
		    return;
		}

		if (juegoTerminado) {
			entorno.cambiarFont("Constantia", 20, Color.WHITE);  
			entorno.escribirTexto(mensajeFinJuego, WIDTH_JUEGO / 2, HEIGHT / 2);
			ticksDesdeFin++;
			if (ticksDesdeFin > 300) System.exit(0);
			return;
		}

		procesarInput();
		actualizarEstadoMurcielagos();
		dibujarTodo();
		verificarFinJuego();
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Juego juego = new Juego();
	}
}
