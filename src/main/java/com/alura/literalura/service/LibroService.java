package com.alura.literalura.service;

import com.alura.literalura.dto.DatosAutor;
import com.alura.literalura.dto.DatosLibro;
import com.alura.literalura.dto.DatosRespuesta;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    private static final String URL_BASE = "https://gutendex.com/books/?search=";

    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConvierteDatos convierteDatos;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private AutorRepository autorRepository;

    public Libro buscarLibroPorTitulo(String tituloBuscado) {
        String tituloCodificado = consumoAPI.codificarValor(tituloBuscado);
        String json = consumoAPI.obtenerDatos(URL_BASE + tituloCodificado);

        DatosRespuesta respuesta = convierteDatos.obtenerDatos(json, DatosRespuesta.class);

        if (respuesta.getResultados() == null || respuesta.getResultados().isEmpty()) {
            return null;
        }

        DatosLibro datosLibro = respuesta.getResultados().get(0);

        Optional<Libro> libroExistente = libroRepository.findByTituloIgnoreCase(datosLibro.getTitulo());
        if (libroExistente.isPresent()) {
            return libroExistente.get();
        }

        DatosAutor datosAutor = null;
        if (datosLibro.getAutores() != null && !datosLibro.getAutores().isEmpty()) {
            datosAutor = datosLibro.getAutores().get(0);
        }

        Autor autor;

        if (datosAutor != null) {
            Optional<Autor> autorExistente = autorRepository.findByNombreIgnoreCase(datosAutor.getNombre());

            if (autorExistente.isPresent()) {
                autor = autorExistente.get();
            } else {
                autor = new Autor(
                        datosAutor.getNombre(),
                        datosAutor.getAnioNacimiento(),
                        datosAutor.getAnioFallecimiento()
                );
                autor = autorRepository.save(autor);
            }
        } else {
            Optional<Autor> autorDesconocido = autorRepository.findByNombreIgnoreCase("Autor desconocido");

            if (autorDesconocido.isPresent()) {
                autor = autorDesconocido.get();
            } else {
                autor = new Autor("Autor desconocido", null, null);
                autor = autorRepository.save(autor);
            }
        }

        String idioma = "desconocido";
        if (datosLibro.getIdiomas() != null && !datosLibro.getIdiomas().isEmpty()) {
            idioma = datosLibro.getIdiomas().get(0);
        }

        Integer numeroDescargas = datosLibro.getNumeroDescargas();
        if (numeroDescargas == null) {
            numeroDescargas = 0;
        }

        Libro libro = new Libro(
                datosLibro.getTitulo(),
                idioma,
                numeroDescargas,
                autor
        );

        libro = libroRepository.save(libro);

        autor.getLibros().add(libro);
        autorRepository.save(autor);

        return libro;
    }

    public List<Libro> listarLibrosRegistrados() {
        return libroRepository.findAll();
    }

    public List<Autor> listarAutoresRegistrados() {
        return autorRepository.findAll();
    }

    public List<Autor> listarAutoresVivosEnAnio(Integer anio) {
        List<Autor> autoresConFallecimiento =
                autorRepository.findByAnioNacimientoLessThanEqualAndAnioFallecimientoGreaterThanEqual(anio, anio);

        List<Autor> autoresSinFallecimiento =
                autorRepository.findByAnioNacimientoLessThanEqualAndAnioFallecimientoIsNull(anio);

        List<Autor> resultado = new ArrayList<>();
        resultado.addAll(autoresConFallecimiento);

        for (Autor autor : autoresSinFallecimiento) {
            boolean existe = false;

            for (Autor autorGuardado : resultado) {
                if (autorGuardado.getId().equals(autor.getId())) {
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                resultado.add(autor);
            }
        }

        return resultado;
    }

    public List<Libro> listarLibrosPorIdioma(String idioma) {
        return libroRepository.findByIdiomaIgnoreCase(idioma);
    }

    public long contarLibrosPorIdioma(String idioma) {
        return libroRepository.countByIdiomaIgnoreCase(idioma);
    }
}