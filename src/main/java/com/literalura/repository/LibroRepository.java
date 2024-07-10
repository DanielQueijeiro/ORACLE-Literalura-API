package com.literalura.repository;

import com.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro,Long> {
    @Query("SELECT l FROM Libro l WHERE LOWER(l.titulo) LIKE LOWER(CONCAT('%', :nombreLibro, '%'))")
    List<Libro> findByTitulo(String nombreLibro);
    List<Libro> findByIdiomasContains(String idioma);
    List<Libro> findTop10ByOrderByTotalDescargasDesc();

}
