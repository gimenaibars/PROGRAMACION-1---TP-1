package juego;

public class Nivel {
    private int numero;
    private int murcielagosObjetivo;
    private double velocidadMurcielagos;
    private String nombre;

    
    // FUNCION PARA SABER EN QUE NIVEL SE ESTA
    public Nivel(int numero, int murcielagosObjetivo, double velocidadMurcielagos, String nombre) {
        this.numero = numero;
        this.murcielagosObjetivo = murcielagosObjetivo;
        this.velocidadMurcielagos = velocidadMurcielagos;
        this.nombre = nombre;        
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
    public String getNombre() {
        return nombre;
    }
}
