package juego;

import entorno.Entorno;
import java.awt.Color;

public class EfectoVisual {
	private int x, y, radio;
	private int duracion;
	private int ticksRestantes;
	
	public EfectoVisual(int x, int y, int radio, int duracion) {
		this.x = x;
		this.y = y;
		this.radio = radio;
		this.duracion = duracion;
		this.ticksRestantes = duracion;
	}
	
	public boolean estaActivo() {
		return ticksRestantes > 0;
	}
	
	public void dibujar(Entorno entorno) {
		if (ticksRestantes > 0) {
			float alpha = (float) ticksRestantes / duracion;
			Color color = new Color(255, 255, 255, (int)(alpha * 120)); // Color del Efecto y transparencia
			entorno.dibujarCirculo(x, y, radio * 2, color);
			ticksRestantes--;
		}
	}
}
