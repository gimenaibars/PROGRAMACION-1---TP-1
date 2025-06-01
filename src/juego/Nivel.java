package juego;

public class Nivel {
	private int numero;
	private int murcielagosObjetivo;
	private double velocidadMurcielagos;
	
	public Nivel(int numero, int murcielagosObjetivo, double velocidadMurcielagos) {
		this.numero = numero;
		this.murcielagosObjetivo = murcielagosObjetivo;
		this.velocidadMurcielagos = velocidadMurcielagos;
	}
	public int getNumero() {
		return numero;
	}
	public int getMurcielagosObjetivo() {
		return murcielagosObjetivo;
	}
	public double getVelocidadMurcielagos() {
		return velocidadMurcielagos;
	}
}	
