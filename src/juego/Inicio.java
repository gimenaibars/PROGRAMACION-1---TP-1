package juego;

import entorno.Entorno;
import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Inicio {
    private Entorno entorno;
    private boolean iniciado = false;
    private int ancho, alto;
    private Image fondo;  
    private float opacidad = 1.0f; // 1.0 = PANTALLA NEGRA, 0.0 = COMPLETAMENTE VISIBLE
    private boolean fadeIn = true;
    private boolean fadeOut = false;


    public Inicio(Entorno entorno, int ancho, int alto) {
        this.entorno = entorno;
        this.ancho = ancho;
        this.alto = alto;
        this.fondo = new ImageIcon(getClass().getResource("/imagenes/fondo_inicio.png")).getImage();
    }
    
    public boolean isIniciado() {
        return iniciado;
    }

    public void actualizar() {
        // VERIFICA QUE LA IMAGEN SE CARGO
        if (fondo == null) {
            System.out.println("Error: la imagen de fondo no se pudo cargar");
        }

        entorno.dibujarImagen(fondo, ancho / 2, alto / 2, 0, 0.17);

        entorno.cambiarFont("Constantia", 18, Color.blue);
        
        // MENSAJE DE LA HISTORIA DE GONDOLF
        String[] mensaje = {
            "En lo profundo de las catacumbas, el anciano mago Gondolf perdió su camino",
            "intentando encontrar una pieza de joyería con forma circular y propiedades mágicas.",
            "Viendo esta oportunidad, su enemigo, el mago Suramun comienza a enviar un sin",
            "fin de sus lacayos murciélagos para intentar derrotarlo."
        };
        
        // INSTRUCCIONES DE JUEGO
        String[] instruccionesLineas = {
            "El Mago Gondolf se podrá mover hacia arriba, abajo, izquierda, derecha,",
            "con las teclas 'w', 'a', 's' y 'd' respectivamente, o con las flechas del teclado.",
            "Tiene dos hechizos para atacar a sus enemigos y dos pociones que le brindarán",
            "protección y vida en caso de necesitarlas."
        };

        int x = ancho / 9;

        int alturaMensaje = mensaje.length * 35; // 4 * 25 = 100
        int alturaInstrucciones = instruccionesLineas.length * 20; // 4 * 20 = 80
        int espacioMensajeInstrucciones = 10;
        int espacioInstruccionesBoton = 30;
        int botonAlto = 45;

        int totalAltura = alturaMensaje + espacioMensajeInstrucciones + alturaInstrucciones +
                          espacioInstruccionesBoton + botonAlto;

        int topY = alto / 2 - totalAltura / 2;

        // DIBUJO DE MENSAJE AJUSTANDO EL TEXTO A LA VENTANAo
        int lineaAlturaMensaje = 25;
        int paddingMensaje = lineaAlturaMensaje / 2;
        for (int i = 0; i < mensaje.length; i++) {
            entorno.escribirTexto(mensaje[i], x, topY + paddingMensaje + i * lineaAlturaMensaje);
        }

        // FUENTE DEL MENSAJE DE INSTRUCCIONES
        entorno.cambiarFont("Constantia", 17, Color.YELLOW);

        int lineaAlturaInstrucciones = 20;
        int paddingInstrucciones = lineaAlturaInstrucciones / 2;
        int instruccionesY = topY + alturaMensaje + espacioMensajeInstrucciones;
        for (int i = 0; i < instruccionesLineas.length; i++) {
            entorno.escribirTexto(instruccionesLineas[i], ancho / 8, instruccionesY + paddingInstrucciones + i * lineaAlturaInstrucciones);
        }

        // CENTRADO DE BOTON DE INICIO
        int botonX = ancho / 2;
        int botonY = instruccionesY + alturaInstrucciones + espacioInstruccionesBoton;
        int botonAncho = 200;
        //int botonAlto = 45;

        // DETECCION DE PUNTERO SOBRE BOTON
        int mouseX = entorno.mouseX();
        int mouseY = entorno.mouseY();
        boolean sobreBoton = mouseX >= botonX - botonAncho / 2 && mouseX <= botonX + botonAncho / 2 &&
                             mouseY >= botonY - botonAlto / 2 && mouseY <= botonY + botonAlto / 2;

        Color colorBoton = sobreBoton ? new Color(173, 216, 230) : Color.WHITE;	//NEW COLOR (173, 216, 230) SI EL MOUSE ESTA SOBRE EL BOTON, SINO ES BLANCO
        Color colorTexto = sobreBoton ? Color.white : Color.BLACK;				// EL TEXTO ES BLANCO SI EL MOUSE ESTA SOBRE EL BOTON, SINO ES NEGRO

        // EFECTO SOMBRA SOBRE BOTON
        Color sombra = new Color(150, 150, 150);	// SOMBRA INFERIOR DERECHA
     
        // FONDO DEL BOTON (BASE)
        entorno.dibujarRectangulo(botonX + 2, botonY + 2, botonAncho, botonAlto, 0, sombra); // SOMBRA
        entorno.dibujarRectangulo(botonX, botonY, botonAncho, botonAlto, 0, colorBoton);     // BOTON PRINCIPAL

        // EFECTO DE BOTON PRECIONADO
        int textoOffset = (sobreBoton && entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO)) ? 2 : 0;
        entorno.cambiarFont("Arial", 20, colorTexto);
        entorno.escribirTexto("INICIAR EL JUEGO", botonX - 85, botonY + 8 + textoOffset);

 // CUANDO SE ACTIVA EL BOTON, COMIENZA EL FADEOUT
    if (sobreBoton && entorno.sePresionoBoton(entorno.BOTON_IZQUIERDO) && !fadeOut) {
        fadeOut = true;
    }

    // MANEJO DE FADE IN/OUT
    if (fadeIn) {
        opacidad -= 0.02f;
        if (opacidad <= 0f) {
            opacidad = 0f;
            fadeIn = false;
        }
    } else if (fadeOut) {
        opacidad += 0.02f;
        if (opacidad >= 1f) {
            opacidad = 1f;
            iniciado = true;
        }
    }

    // RECTANGULO NEGRO CON OPACIDAD (SIMULA FADE)
    Color overlay = new Color(0f, 0f, 0f, Math.min(1f, Math.max(0f, opacidad)));
    entorno.dibujarRectangulo(ancho / 2, alto / 2, ancho, alto, 0, overlay);

    }
   }