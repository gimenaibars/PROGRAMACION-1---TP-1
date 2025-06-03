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
	private Entorno entorno;
	private Inicio pantallaInicio;
    private Image fondo;
    private Image fondomenu;

    public static final int WIDTH_JUEGO = 600;
    public static final int HEIGHT = 600;
    public static final int WIDTH_TOTAL = 800;
    
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
    private JefeFinal jefeFinal;
    
    private ArrayList<Pocion> pociones = new ArrayList<>();
    private long tiempoUltimaPocionVida = 0;
    private long tiempoUltimaPocionProteccion = 0;


    private int enemigosEliminados;
    private int enemigosGenerados = 0;
    private final int MAX_ENEMIGOS_VIVOS = 5;		//CANTIDAD DE ENEMIGOS EN PANTALLA A LA VEZ
    private final int TOTAL_ENEMIGOS = 300;			//CANTIDAD DE ENEMIGOS QUE APARECERAN

    private boolean juegoTerminado = false;
    private String mensajeFinJuego = "";
    private int ticksDesdeFin = 0;
    private boolean mostrarMensajeInicio = true;
    private int ticksInicio = 0;
    
    private boolean enPausa = false;
    private boolean pPresionadaAnteriormente = false;

    private Random rand = new Random();
    

    public Juego() {
    	//CONSTRUCTOR DEL JUEGO 
    	// INICIALIZA EL OBJETO ENTORNO		
        this.entorno = new Entorno(this, "El camino de Gondolf - Grupo 13", WIDTH_TOTAL, HEIGHT);
        this.fondo = new ImageIcon(getClass().getResource("/imagenes/fondo.png")).getImage();
        this.fondomenu = new ImageIcon(getClass().getResource("/imagenes/fondomenu.png")).getImage();
        this.gondolf = new Gondolf(300, 300);
        pantallaInicio = new Inicio(entorno, WIDTH_TOTAL, HEIGHT);
        Herramientas.loop("sonido/musicadefondo.wav"); // MUSICA DE FONDO EN (all moment)

        //INICIO DE NIVELES
        niveles = new Nivel[] {
            new Nivel(1, 5, 0.5, ""),	//NIVEL, CANTIDAD ENEMIGOS, VELOCIDAD, NOMBRE
            new Nivel(2, 10, 0.8, ""),	//NIVEL, CANTIDAD ENEMIGOS, VELOCIDAD, NOMBRE
            new Nivel(3, 20, 1.0, ""),	//NIVEL, CANTIDAD ENEMIGOS, VELOCIDAD, NOMBRE
            new Nivel(4, 300, 2.0, "JUEGO FINAL" )	//NIVEL, CANTIDAD ENEMIGOS, VELOCIDAD, NOMBRE
        };
        
        // ARREGLO DE NIVELES 
        nivelActualIndex = 0;
        nivelActual = niveles[nivelActualIndex];

        //LISTA UBICACION DE ROCAS
        this.rocas = new ArrayList<>();
        rocas.add(new Roca(150, 150));
        rocas.add(new Roca(400, 250));
        rocas.add(new Roca(300, 500));
        rocas.add(new Roca(100, 400));
        rocas.add(new Roca(500, 100));
        
        //LISTA CREACION DE MURCIELAGOS
        this.murcielagos = new ArrayList<>();

        //LISTA DE HECHIZOS
        this.hechizos = new ArrayList<>();
        hechizos.add(new Hechizo("Fuego", 120, 30, 40, new Color(242, 62, 26, 20)));	//NOMBRE, AREA, COSTO MAGIA, DAÑO, COLOR
        hechizos.add(new Hechizo("Hielo", 120, 20, 10, new Color(36, 108, 236, 20)));	//NOMBRE, AREA, COSTO MAGIA, DAÑO, COLOR

        this.menu = new Menu();
        
        // INICIA EL JUEGO
        this.entorno.iniciar();
    }

//********************  COMIENZO DE LAS FUNCIONES  ***************************************
    
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

    //*****************  CLASE AUXILIAR AREAHECHIZO****************************************
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
            float proporcion = (float) ticksRestantes / 30f; 
            int alpha = (int)(color.getAlpha() * proporcion);
            alpha = Math.max(0, alpha);		//NOS ASEGURAMOS QUE NO SEA MENOR A 0

            Color colorFade = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
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

        if (nivelActual.getNumero() != 4) {
            for (Murcielago m : murcielagos) {
                m.actualizarEstado(tiempoActual);
                if (m.estaVivo()) {
                	
                }
                    // SI GOLDOLF TIENE PROTECCION LOS MURCIELAGOS NO SE ACERCAN
                if (m.estaVivo()) {
                    if (gondolf.estaProtegido() && dentroDelRango(m.getX(), m.getY(), gondolf.getX(), gondolf.getY(), 80)) {
                        m.moverAfueraDe(gondolf.getX(), gondolf.getY());
                    } else {
                        m.perseguir(gondolf, nivelActual.getVelocidadMurcielagos());
                        m.atacarSiColisiona(gondolf);
                    }
                }
            }
        
       
        // SI ES NIVEL 4 NO SE GENERAN MURCIELAGOS
            
            murcielagos.removeIf(m -> {
                if (!m.estaVivo() && !m.estaCongelado() && !m.estaQuemado()) {
                    if (m.fueEliminadoPorJugador()) {
                        enemigosEliminados++;	//SUMA DE A UN MURCIELAGO EL CONTADOR CUANDO MUEREN
                    }
                    return true;
                }
                return false;
            });
        }
    }
    private void procesarInput() {
    	
    	// MOVIMIENTOS DE GOLDOLF
    	// MOVIMIENTOS CON TECLAS Y FLECHAS
        double dx = 0, dy = 0;
        if (entorno.estaPresionada(entorno.TECLA_DERECHA) || entorno.estaPresionada('d')) dx = 5;
        if (entorno.estaPresionada(entorno.TECLA_IZQUIERDA) || entorno.estaPresionada('a')) dx = -5;
        if (entorno.estaPresionada(entorno.TECLA_ARRIBA) || entorno.estaPresionada('w')) dy = -5;
        if (entorno.estaPresionada(entorno.TECLA_ABAJO) || entorno.estaPresionada('s')) dy = 5;

        gondolf.regenerarMagia();
        gondolf.mover(dx, dy, rocas);
        
        // SI SE PRSIONA BOTON IZQUIERDO DEL MOUSE
        if (entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) {
            int mx = entorno.mouseX();
            int my = entorno.mouseY();

            if (mx > WIDTH_JUEGO) {
                menu.seleccionarHechizo(mx, my, hechizos, gondolf.getMagia());
            } else {
                Hechizo h = menu.getHechizoSeleccionado();

                if (h != null && gondolf.puedeUsarHechizo(h.getCostoMagia())) {
                    int centroX = (int) gondolf.getX();
                    int centroY = (int) gondolf.getY();

                    areasHechizo.add(new AreaHechizo(centroX, centroY, h.getArea(), h.getColor(), 30));
                    h.lanzar(centroX, centroY, murcielagos, gondolf); // Solo se llama una vez
                    
                    
                    if (h.getNombre().equals("Fuego")) {
                        Herramientas.play("sonido/musicafuego.wav");
                    } else if (h.getNombre().equals("Hielo")) {
                        Herramientas.play("sonido/musicahielo.wav");
                    }
                    

                    if (jefeFinal != null && jefeFinal.estaVivo() &&
                        dentroDelRango(jefeFinal.getX(), jefeFinal.getY(), centroX, centroY, h.getArea())) {
                        jefeFinal.recibirDaño(h.getDaño());
                    }

                    gondolf.consumirMagia(h.getCostoMagia());
                    menu.deseleccionar();

                } else if (h == null) {
                    // DETECTA SI SE HIZO CLICK SOBRE EL JEFE
                    if (jefeFinal != null && jefeFinal.estaVivo()) {
                        double jefeX = jefeFinal.getX();
                        double jefeY = jefeFinal.getY();
                        int anchoJefe = 30;
                        int altoJefe = 30;

                        if (mx >= jefeX - anchoJefe / 2 && mx <= jefeX + anchoJefe / 2 &&
                            my >= jefeY - altoJefe / 2 && my <= jefeY + altoJefe / 2) {
                        	Herramientas.play("sonido/musicaefectoaplastar.wav"); // SONIDO AL CLICKEAR SOBRE EL JEFE
                            jefeFinal.recibirDaño(5);
                        }
                    }

                    // DETECTA SI SE HIZO CLICK SOBRE UN MURCIELAGO
                    for (Murcielago m : murcielagos) {
                        if (m.estaVivo() && dentroDelRango(m.getX(), m.getY(), mx, my, 20)) {
                            m.eliminarSinAnimacion();
                            m.marcarComoEliminadoPorJugador();
                            efectos.add(new EfectoVisual((int) m.getX(), (int) m.getY(), 20, 15));
                            Herramientas.play("sonido/musicaefectoaplastar.wav"); // SONIDOS AL CLICKEAR SOBRE LOS MURCIELAGOS
                            break;
                        }
                    }
                }
            }
        }
    }
               
    private void dibujarTodo() {
        int anchoOriginal = fondo.getWidth(null);
        int altoOriginal = fondo.getHeight(null);
        double escalaX = (double) WIDTH_JUEGO / anchoOriginal;
        double escalaY = (double) HEIGHT / altoOriginal;
        double escala = Math.min(escalaX, escalaY);
        entorno.dibujarImagenConCentro(fondo, WIDTH_JUEGO / 2, HEIGHT / 2, anchoOriginal / 2.0, altoOriginal / 2.0, 0, escala);

        areasHechizo.removeIf(area -> !area.estaActivo());
        for (AreaHechizo area : areasHechizo) {
            area.dibujar(entorno);
        }

        gondolf.dibujar(entorno);
        for (Roca r : rocas) r.dibujar(entorno);
        for (Murcielago m : murcielagos)
            if (m.estaVivo()) m.dibujar(entorno);
        
        //DIBUJA UN CUADRO Y SE MUESTRA LA VIDA DEL JEFE EN NUMERO
        
        if (jefeFinal != null && jefeFinal.estaVivo()) {
            jefeFinal.dibujar(entorno);

            // TEXTO Y POSICION
            String texto = "JEFE: " + jefeFinal.getVida();
            int x = 30;
            int y = 60;
            int paddingX = 10;
            int paddingY = 6;
            int anchoTexto = 120;
            int altoTexto = 30;

            // FONDO DEL RECUADRO
            entorno.dibujarRectangulo(
                x + anchoTexto / 2,
                y - altoTexto / 2 + paddingY,
                anchoTexto,
                altoTexto,
                0,
                new Color(0, 0, 0, 180)
            );

            // BORDE BLANCO DEL RECUADRO
            entorno.dibujarRectangulo(
                x + anchoTexto / 2,
                y - altoTexto / 2 + paddingY,
                anchoTexto,
                altoTexto,
                0,
                Color.ORANGE
            );

            // TEXTO ENCIMA
            entorno.cambiarFont("CENTURY", 20, Color.BLACK);
            entorno.escribirTexto(texto, x + paddingX, y);
        }
        menu.dibujar(entorno, this.fondomenu, hechizos, menu.getHechizoSeleccionado(), gondolf.getVida(), gondolf.getMagia(), enemigosEliminados);

        efectos.removeIf(e -> !e.estaActivo());
        for (EfectoVisual ef : efectos) {
            ef.dibujar(entorno);
        }     
    }
    
    private void verificarDerrotaJefeFinal() {
        if (nivelActual.getNumero() == 4 && jefeFinal != null && !jefeFinal.estaVivo()) {
            juegoTerminado = true;
            mensajeFinJuego = "¡Has ganado! Has derrotado al Jefe Final.";
            ticksDesdeFin = 0;
        }
    }
    
    private void avanzarNivel() {
        nivelActualIndex++;
        if (nivelActualIndex < niveles.length) {
            nivelActual = niveles[nivelActualIndex];
            enemigosEliminados = 0;
            enemigosGenerados = 0;
            murcielagos.clear();
            mostrarTransicionNivel = true;
            ticksTransicionNivel = 0;

            if (nivelActual.getNumero() == 4) {
                jefeFinal = new JefeFinal(WIDTH_JUEGO / 2, HEIGHT / 2);
            } else {
                jefeFinal = null;
            }
        } else {
            juegoTerminado = true;
            mensajeFinJuego = "¡Has ganado! Todos los niveles completados.";
            ticksDesdeFin = 0;
        }
    }

    private void verificarFinNivel() {
        if (nivelActual.getNumero() != 4 && enemigosEliminados >= nivelActual.getMurcielagosObjetivo()) {
            avanzarNivel();
        }
    }
     
    // FUNCIONES DE POCIONES
    private void actualizarPociones() {
        long tiempoActual = System.currentTimeMillis();
        
        // SI ESTA EN EL NIVEL 4 LAS POCIONES APARECEN POR CADA CIERTO TIEMPO
        if (nivelActual.getNumero() != 4) {
        	
            // NIVELES 1 AL 3: SE GENERAN DE A CUERDO A LA CANTIDAD DE ENEMIGOS ELIMINADOS
        	
        	// SI SE ELIMINAN >=15 ENEMIGOS APARECE LA POCION DE VIDA
            if (enemigosEliminados >= 15 && 
                pociones.stream().noneMatch(p -> p.getTipo() == Pocion.Tipo.VIDA)) {
                double[] pos = generarPosicionValida();
                pociones.add(new Pocion(pos[0], pos[1], "vida"));
            }
            
            // SI SE ELIMINAN >=8 ENEMIGOS APARECE LA POCION DE PROTECCION
            if (enemigosEliminados >= 8 && 
                pociones.stream().noneMatch(p -> p.getTipo() == Pocion.Tipo.PROTECCION)) {
                double[] pos = generarPosicionValida();
                pociones.add(new Pocion(pos[0], pos[1], "proteccion"));
            }
        } else {
        	
            // NIVEL 4: GENERACION POR TIEMPOS
        	
        	// PASADOS 8 SEGUNDOS APARECE LA POCION DE PROTECCION
            if (tiempoActual - tiempoUltimaPocionProteccion > 8000) { // 8 SEGUNDOS
                double[] pos = generarPosicionValida();
                pociones.add(new Pocion(pos[0], pos[1], "proteccion"));
                tiempoUltimaPocionProteccion = tiempoActual;
            }
            
            // PASADOS 15 SEGUNDOS APARECE LA POCION DE VIDA
            if (tiempoActual - tiempoUltimaPocionVida > 15000) { // 15 SEGUNDOS
                double[] pos = generarPosicionValida();
                pociones.add(new Pocion(pos[0], pos[1], "vida"));
                tiempoUltimaPocionVida = tiempoActual;
            }
        }
    }

    private boolean estaSobreRoca(double x, double y) {
        for (Roca r : rocas) {
            if (dentroDelRango(x, y, r.getX(), r.getY(), 40)) { // 40 = RADIO SEGURO PARAQUE NO APAREZCA SOBRE ROCAS
                return true;
            }
        }
        return false;
    }

    private double[] generarPosicionValida() {
        double x, y;
        do {
            x = rand.nextDouble() * 550;
            y = rand.nextDouble() * 550;
        } while (estaSobreRoca(x, y));
        return new double[]{x, y};
    }

//****************************  CICLO DEL JUEGO  ***************************
    
    public void tick() {
    	
    	// PROCESAMIENTO DE UN INSTANTE DE TIEMPO
    
    	if (!pantallaInicio.isIniciado()) {
    	    pantallaInicio.actualizar();
    	    return; // ESPERA A QUE SE PRESIONE "INICIAR"
    	    }
    	
    	// PAUSA/REANUDAR AL PRESIONAS LA LETRA "P"
    	if (entorno.estaPresionada('p')) {
    	    if (!pPresionadaAnteriormente) {
    	        enPausa = !enPausa; 	// CAMBIA EL ESTADO DE PAUSA
    	        pPresionadaAnteriormente = true;
    	    }
    	} else {
    	    pPresionadaAnteriormente = false;
    	}

    	// SI ESTA EN PAUSA, SOLO SE DIBUJA EL MENSAJE Y SE RETOMA
    	if (enPausa) {
    	    dibujarTodo(); // DIBUJA EL ESTADO ACTUAL
    	    entorno.cambiarFont("Arial", 30, Color.YELLOW);
    	    entorno.escribirTexto("JUEGO EN PAUSA", 200, HEIGHT / 2);
    	    return;
    	}
    	
    	// MUESTRA MENSAJE INICIO DE JUEGO    	 
        if (mostrarMensajeInicio) {
            entorno.cambiarFont("Constantia", 20, Color.WHITE);
            entorno.escribirTexto("INICIO DEL JUEGO", WIDTH_JUEGO / 2, HEIGHT / 2);
            ticksInicio++;
            if (ticksInicio > 180) mostrarMensajeInicio = false;
            return;
        }
        
        for (Pocion p : pociones) {
            if (p.estaActiva() && p.colisionaCon(gondolf)) {
            	Herramientas.play("sonido/musicapocion.wav");
                if (p.getTipo() == Pocion.Tipo.VIDA) {
                    gondolf.recuperarVida(100); // RECUPERA EL TOTAL DE LA VIDA (100)
                } else if (p.getTipo() == Pocion.Tipo.PROTECCION) {
                    gondolf.activarProteccion(5000); // 5 SEGUNDOS DE PROTECCION
                }
                p.desactivar();
            }
        }

        if (mostrarTransicionNivel) {
            entorno.cambiarFont("Constantia", 20, Color.YELLOW);
            String mensajeNivel = nivelActual.getNumero() == 4
                ? nivelActual.getNombre()
                : "NIVEL " + nivelActual.getNumero() + " - ELIMINA " + nivelActual.getMurcielagosObjetivo() + " MURCIÉLAGOS";
            entorno.escribirTexto(mensajeNivel, WIDTH_JUEGO / 3, HEIGHT / 2);
            ticksTransicionNivel++;
            if (ticksTransicionNivel > duracionMensajeNivel) {
                mostrarTransicionNivel = false;
            }
            return;
        }
        
        if (!juegoTerminado) {
            procesarInput();
            actualizarPociones(); 

            if (nivelActual.getNumero() != 4) {
                if (enemigosGenerados < TOTAL_ENEMIGOS && contarMurcielagosVivos() < MAX_ENEMIGOS_VIVOS) {
                    murcielagos.add(crearMurcielagoFueraPantalla());
                    enemigosGenerados++;
                }
                actualizarEstadoMurcielagos();
            } else if (jefeFinal != null && jefeFinal.estaVivo()) {
                jefeFinal.actualizarEstado(gondolf);
            }

            verificarFinNivel();
            verificarDerrotaJefeFinal();

            if (!gondolf.estaVivo()) {
                juegoTerminado = true;
                mensajeFinJuego = "¡Has perdido! Gondolf murió.";
                ticksDesdeFin = 0;
            }
        }
        
        dibujarTodo();
        
        for (Pocion p : pociones) {
            if (p.estaActiva()) {
                p.dibujar(entorno);
            }
        }

        if (juegoTerminado) {
        	// DIBUJA FONDO NEGRO
        	entorno.dibujarRectangulo(WIDTH_JUEGO / 2.0, HEIGHT / 2.0, WIDTH_JUEGO * 1.0, HEIGHT * 1.0, 0.0, Color.BLACK);

            // CONFIGURACION DE FUENTE Y MENSAJE
            entorno.cambiarFont("Constantia", 24, Color.RED);
            entorno.escribirTexto(mensajeFinJuego, WIDTH_JUEGO / 2 - 150, HEIGHT / 2);
            
            // CONTADOR PARA SALIDA AUTOMATICA
            ticksDesdeFin++;
            if (ticksDesdeFin > 300) {
                System.exit(0);
            }
        }
    }
    
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Juego juego = new Juego();
	}
}

