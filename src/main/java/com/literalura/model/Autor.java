package com.literalura.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long autor_id;


    private String nombre;
    private int fechaNacimiento;
    private int fechaFallecimiento;

    @ManyToMany(mappedBy = "autor", fetch = FetchType.EAGER)
    private List<Libro> libros;

    public Autor() {}

    public Autor(List<DatosAutor> datosAutor) {
        this.nombre = datosAutor.get(0).nombre();
        this.fechaNacimiento = datosAutor.get(0).fechaNacimiento();
        this.fechaFallecimiento = datosAutor.get(0).fechaFallecimiento();
        this.libros = new ArrayList<>();
    }

    @Override
    public String toString() {
        String infoLibros = libros.stream()
                .map(Libro::getTitulo)
                .collect(Collectors.joining(" & "));
        return "Nombre: " + nombre + "\n" +
                "Libros publicados: " + infoLibros + "\n" +
                "Año de nacimiento: " + fechaNacimiento + "\n" +
                "Año de Fallecimiento:" + fechaFallecimiento + "\n";
    }

    public String getNombre() {
        return nombre;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    public void addLibro(Libro libro) {
        this.libros.add(libro);
    }
}
