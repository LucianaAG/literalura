package com.alura.literalura.service;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal {

    @Autowired
    private LibroService libroService;

    private final Scanner teclado = new Scanner(System.in);

    public void mostrarMenu() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("""
                    
                    ===== LITERALURA =====
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma
                    6 - Mostrar cantidad de libros por idioma
                    0 - Salir
                    
                    Elige una opción:
                    """);

            try {
                opcion = Integer.parseInt(teclado.nextLine());

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
                        listarAutoresVivosEnAnio();
                        break;
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 6:
                        mostrarCantidadPorIdioma();
                        break;
                    case 0:
                        System.out.println("Cerrando aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Debes ingresar un número válido.");
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }
    }

    private void buscarLibroPorTitulo() {
        System.out.println("Escribe el título del libro:");
        String titulo = teclado.nextLine();

        Libro libro = libroService.buscarLibroPorTitulo(titulo);

        if (libro == null) {
            System.out.println("No se encontró ningún libro en la API.");
        } else {
            System.out.println("Libro encontrado y guardado:");
            System.out.println(libro);
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroService.listarLibrosRegistrados();

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
            return;
        }

        for (Libro libro : libros) {
            System.out.println(libro);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = libroService.listarAutoresRegistrados();

        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
            return;
        }

        for (Autor autor : autores) {
            System.out.println(autor);
            System.out.println("Libros:");

            if (autor.getLibros() == null || autor.getLibros().isEmpty()) {
                System.out.println("- Sin libros asociados");
            } else {
                for (Libro libro : autor.getLibros()) {
                    System.out.println("- " + libro.getTitulo());
                }
            }

            System.out.println();
        }
    }

    private void listarAutoresVivosEnAnio() {
        System.out.println("Ingresa el año:");
        String entrada = teclado.nextLine();

        try {
            Integer anio = Integer.parseInt(entrada);
            List<Autor> autores = libroService.listarAutoresVivosEnAnio(anio);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores vivos en ese año.");
                return;
            }

            for (Autor autor : autores) {
                System.out.println(autor);
            }

        } catch (NumberFormatException e) {
            System.out.println("Debes ingresar un año válido.");
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("""
                Ingresa el idioma:
                en = inglés
                es = español
                fr = francés
                pt = portugués
                """);

        String idioma = teclado.nextLine();

        List<Libro> libros = libroService.listarLibrosPorIdioma(idioma);

        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados en ese idioma.");
            return;
        }

        for (Libro libro : libros) {
            System.out.println(libro);
        }
    }

    private void mostrarCantidadPorIdioma() {
        long cantidadEn = libroService.contarLibrosPorIdioma("en");
        long cantidadEs = libroService.contarLibrosPorIdioma("es");

        System.out.println("\n===== ESTADÍSTICAS =====");
        System.out.println("Cantidad de libros en inglés (en): " + cantidadEn);
        System.out.println("Cantidad de libros en español (es): " + cantidadEs);
        System.out.println("========================");
    }
}