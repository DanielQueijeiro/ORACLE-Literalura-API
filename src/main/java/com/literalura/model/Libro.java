package com.literalura.model;

import jakarta.persistence.*;
import com.literalura.model.Autor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long libro_id;
    @Column
    private String titulo;
    private int totalDescargas;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "libro_idiomas", joinColumns = @JoinColumn(name = "libro_id"))
    @Column(name = "idioma")
    private List<String> idiomas;


    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "autores_libros",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autor;

    public Libro() {}

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.idiomas = datosLibro.idiomas();
        this.totalDescargas = datosLibro.totalDescargas();
        this.autor = new ArrayList<>();
    }

    public void setAutor(List<Autor> autor) {
        this.autor = autor;
    }

    public void addAutor(Autor autor) {
        this.autor.add(autor);
    }

    public String getTitulo() {
        return titulo;
    }

    @Override
    public String toString() {
        String infoAutor = autor.stream()
                .map(Autor::getNombre)
                .collect(Collectors.joining(" & "));
        return "Titulo: " + titulo + '\n' +
                "Autor: " + infoAutor + '\n' +
                "Idiomas: " + idiomas + '\n' +
                "Total de descargas: " + totalDescargas + '\n';

    }
}
