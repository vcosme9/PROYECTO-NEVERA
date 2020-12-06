package com.example.proyectovinoteca;

public interface RepositorioGlobal {
    Vino elemento(int id);
    void nuevo();
    void borrar(int id);
    int tamaño();
    void actualiza(int id, Vino vino);
    void añade(Vino vino);

}
