package com.literalura.principal;

import com.literalura.model.*;
import com.literalura.repository.AutorRepository;
import com.literalura.repository.LibroRepository;
import com.literalura.service.ConsumoAPI;
import com.literalura.service.ConvierteDatos;

import java.util.List;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL = "https://gutendex.com/books";
    private ConvierteDatos conversor = new ConvierteDatos();
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private List<DatosLibro> datosLibro;
    private List<Libro> libros;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Registrar libro por titulo o autor
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    6 - Listar top 10 libros
                    7 - Buscar libros por titulo
                    8 - Buscar autores por nombre
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            try {
                opcion = teclado.nextInt();
            } catch (Exception e) {
                System.out.println("Opción no válida. Intente nuevamente.");
                teclado.nextLine();
                continue;
            }
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarLibroPorTitulo();
                    break;
                case 2:
                    listarLibrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivosEnUnDeterminadoAnio();
                    break;
                case 5:
                    listarLibrosPorIdioma();
                    break;
                case 6:
                    listarTop10Libros();
                    break;
                case 7:
                    buscarLibroRegistrado();
                    break;
                case 8:
                    buscarAutorPorNombre();
                    break;
                case 0:
                    System.out.println("Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
    }

    private DatosLibro getDatosLibro() {
        System.out.println("Ingrese el titulo del libro o autor a buscar:");
        var titulo = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL + "/?search=" + titulo.replace(" ", "%20"));
        DatosAPI datosAPI = conversor.obtenerDatos(json, DatosAPI.class);
        DatosLibro datos;
        try {
            datos = datosAPI.datosLibro().get(0);
        } catch (Exception e) {
            return null;
        }
        return datos;
    }

    private void buscarLibroPorTitulo() {
        if(getDatosLibro() == null) {
            System.out.println("No se encontraron libros con ese título.");
            return;
        }
        DatosLibro datos = getDatosLibro();

        Libro libro = new Libro(datos);

        for (DatosAutor datosAutor : datos.autor()){
            Autor autor = autorRepository.findByNombre(datosAutor.nombre());
            if (autor == null) {
                autor = new Autor(List.of(datosAutor));
                autorRepository.save(autor);
            }
            autor.addLibro(libro);
            libro.addAutor(autor);
        }

        List<Libro> libroBuscar = libroRepository.findByTitulo(datos.titulo());
        if (!libroBuscar.isEmpty()) {
            System.out.println("El libro ya se encuentra registrado");
            return;
        }

        libroRepository.save(libro);

        System.out.println("\n\n********** Libro registrado **********");
        System.out.println(libro.toString());
        System.out.println("**************************************\n\n");

    }

    private void listarLibrosRegistrados() {
        libros = libroRepository.findAll();
        System.out.println("\n\n********** Libros registrados **********");
        libros.forEach(libro -> System.out.println(libro.toString()));
        System.out.println("**************************************\n\n");
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        System.out.println("\n\n********** Autores registrados **********");
        autores.forEach(autor -> System.out.println(autor.toString()));
        System.out.println("**************************************\n\n");
    }

    private void listarAutoresVivosEnUnDeterminadoAnio() {
        System.out.println("Ingrese el año:");
        var year = teclado.nextInt();
        teclado.nextLine();
        List<Autor> autores = autorRepository.findByYearAlive(year);
        if(!autores.isEmpty()) {
            System.out.println("\n\n********** Autores vivos en el año " + year + " **********");
            autores.forEach(autor -> System.out.println(autor.toString()));
            System.out.println("**************************************\n\n");
        } else {
            System.out.println("No se encontraron autores vivos en el año " + year);
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma para buscar los libros:\n"
        + "es - Español\n" + "en - Inglés\n" + "fr - Francés\n" + "pt - Portugués\n");
        var idioma = teclado.nextLine();
        List<Libro> libros = libroRepository.findByIdiomasContains(idioma);
        if(!libros.isEmpty()) {
            System.out.println("\n\n********** Libros en el idioma " + idioma + " **********");
            libros.forEach(libro -> System.out.println(libro.toString()));
            System.out.println("**************************************\n\n");
        } else {
            System.out.println("No se encontraron libros en ese idioma");
        }
    }

    private  void listarTop10Libros() {
        final int[] contador = {1};
        List<Libro> libros = libroRepository.findTop10ByOrderByTotalDescargasDesc();
        if(!libros.isEmpty()) {
            System.out.println("\n\n********** Top 10 libros **********");
            libros.forEach(libro -> System.out.println(contador[0]++ + "-\n" + libro.toString()));
            System.out.println("**************************************\n\n");
        } else {
            System.out.println("No se encontraron libros");
        }
    }

    private void buscarLibroRegistrado(){
        System.out.println("Ingrese el titulo del libro a buscar:");
        var tituloLibro = teclado.nextLine();
        List<Libro> libros = libroRepository.findByTitulo(tituloLibro);
        if(!libros.isEmpty()) {
            System.out.println("\n\n********** Datos encontrados **********");
            libros.forEach(libro -> System.out.println(libro.toString()));
            System.out.println("**************************************\n\n");
        } else {
            System.out.println("No se encontraron datos del libro");
        }
    }

    private void buscarAutorPorNombre() {
        System.out.println("Ingrese el nombre del autor a buscar:");
        var nombreAutor = teclado.nextLine();
        List<Autor> autores = autorRepository.findAutorByNombre(nombreAutor);
        if(!autores.isEmpty()) {
            System.out.println("\n\n********** Datos encontrados **********");
            autores.forEach(a -> System.out.println(a.toString()));
            System.out.println("**************************************\n\n");
        } else {
            System.out.println("No se encontraron datos del autor");
        }
    }

}
