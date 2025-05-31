package juego;

import java.awt.Color;
import java.awt.Image;
import java.util.Random;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import entorno.Entorno;
import entorno.InterfaceJuego;

public class Juego extends InterfaceJuego {
	private Image fondo;

	public static final int WIDTH_JUEGO = 600;
	public static final int HEIGHT = 600;
	public static final int WIDTH_TOTAL = 800;

	private Entorno entorno;
	private Gondolf gondolf;
	private ArrayList<Roca> rocas;
	private ArrayList<Murcielago> murcielagos;
	private ArrayList<Hechizo> hechizos;
	private Menu menu;

	private int enemigosEliminados;
	private int enemigosGenerados = 0;
	private final int MAX_ENEMIGOS_VIVOS = 20;
	private final int TOTAL_ENEMIGOS = 150;

	private boolean juegoTerminado = false;
	private String mensajeFinJuego = "";
	private int ticksDesdeFin = 0;
	private boolean mostrarMensajeInicio = true;
	private int ticksInicio = 0;

	private Random rand = new Random();

	public Juego() {
		this.entorno = new Entorno(this, "El camino de Gondolf - Grupo 13", WIDTH_TOTAL, HEIGHT);
		this.fondo = new ImageIcon(getClass().getResource("/imagenes/fondo.png")).getImage();

		this.gondolf = new Gondolf(300, 300);

		this.rocas = new ArrayList<>();
		rocas.add(new Roca(150, 150));
		rocas.add(new Roca(400, 250));
		rocas.add(new Roca(300, 500));
		rocas.add(new Roca(100, 400));
		rocas.add(new Roca(500, 100));

		this.murcielagos = new ArrayList<>();

		this.hechizos = new ArrayList<>();
		hechizos.add(new Hechizo("Fuego", 100, 30, Color.black));
		hechizos.add(new Hechizo("Hielo", 80, 20, Color.black));

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
				m.perseguir(gondolf);
				m.atacarSiColisiona(gondolf);
			}
		}

		murcielagos.removeIf(m -> {
			if (!m.estaVivo() && !m.estaCongelado() && !m.estaQuemado()) {
				enemigosEliminados++; // Aquí sumás 1 vez por murciélago eliminado
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
		// Movimiento
		double dx = 0, dy = 0;
		if (entorno.estaPresionada(entorno.TECLA_DERECHA)) dx = 5;
		if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA)) dx = -5;
		if (entorno.estaPresionada(entorno.TECLA_ARRIBA)) dy = -5;
		if (entorno.estaPresionada(entorno.TECLA_ABAJO)) dy = 5;

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

	private void dibujarTodo() {
		// Fondo escalado
		int anchoOriginal = fondo.getWidth(null);
		int altoOriginal = fondo.getHeight(null);
		double escalaX = (double) WIDTH_JUEGO / anchoOriginal;
		double escalaY = (double) HEIGHT / altoOriginal;
		double escala = Math.min(escalaX, escalaY);

		entorno.dibujarImagenConCentro(fondo, WIDTH_JUEGO / 2, HEIGHT / 2, anchoOriginal / 2.0, altoOriginal / 2.0, 0, escala);

		// Personajes
		gondolf.dibujar(entorno);
		for (Roca r : rocas) r.dibujar(entorno);
		for (Murcielago m : murcielagos)
			if (m.estaVivo()) m.dibujar(entorno);

		menu.dibujar(entorno, hechizos, menu.getHechizoSeleccionado(), gondolf.getVida(), gondolf.getMagia(), enemigosEliminados);
	}

	private void verificarFinJuego() {
		if (!gondolf.estaVivo()) {
			juegoTerminado = true;
			mensajeFinJuego = "¡Has perdido! Gondolf murió.";
			ticksDesdeFin = 0;
		} else if (enemigosEliminados >= TOTAL_ENEMIGOS) {
			juegoTerminado = true;
			mensajeFinJuego = "¡Has ganado! Todos los murciélagos eliminados.";
			ticksDesdeFin = 0;
		}
	}

	public void tick() {
		if (juegoTerminado) {
			entorno.cambiarFont("Constantia", 20, Color.WHITE);  // Correcto uso de tu método
			entorno.escribirTexto(mensajeFinJuego, WIDTH_JUEGO / 2, HEIGHT / 2);
			ticksDesdeFin++;
			if (ticksDesdeFin > 300) System.exit(0);
			return;
		}

		if (mostrarMensajeInicio) {
			entorno.cambiarFont("Constantia", 20, Color.WHITE);  // Correcto uso de tu método
			entorno.escribirTexto("INICIO DEL JUEGO", WIDTH_JUEGO / 2, HEIGHT / 2);
			ticksInicio++;
			if (ticksInicio > 180) mostrarMensajeInicio = false;
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
