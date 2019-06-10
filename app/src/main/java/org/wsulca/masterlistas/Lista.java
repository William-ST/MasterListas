package org.wsulca.masterlistas;

public class Lista {

    private int imagen;
    private String nombre;
    private int elementos;

    public Lista(int imagen, String nombre, int elementos) {
        this.imagen = imagen;
        this.nombre = nombre;
        this.elementos = elementos;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getElementos() {
        return elementos;
    }

    public void setElementos(int elementos) {
        this.elementos = elementos;
    }

}
