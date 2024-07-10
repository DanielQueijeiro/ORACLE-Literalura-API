package com.literalura.repository;

import com.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long>{
    Autor findByNombre(String nombre);

    @Query("SELECT a FROM Autor a WHERE (:year BETWEEN a.fechaNacimiento AND a.fechaFallecimiento) AND a.fechaNacimiento <> 0")
    List<Autor> findByYearAlive(int year);

    @Query("SELECT a FROM Autor a WHERE LOWER(a.nombre) LIKE LOWER(CONCAT('%', :nombreAutor, '%'))")
    List<Autor> findAutorByNombre(String nombreAutor);
}
