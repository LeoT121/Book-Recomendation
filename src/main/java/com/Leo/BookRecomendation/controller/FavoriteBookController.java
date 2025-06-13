package com.Leo.BookRecomendation.controller;

import com.Leo.BookRecomendation.entity.FavoriteBook;
import com.Leo.BookRecomendation.service.FavoriteBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/libros")
public class FavoriteBookController {

    @Autowired
    private FavoriteBookService libroFavoritoService;

    /**
     * Verifica si un libro está en favoritos
     */
    @GetMapping("/favoritos/verificar/{libroId}")
    @ResponseBody
    public ResponseEntity<?> verificarFavorito(@PathVariable String libroId) {
        Map<String, Object> response = new HashMap<>();
        boolean esFavorito = libroFavoritoService.isFavorite(libroId);
        response.put("esFavorito", esFavorito);
        return ResponseEntity.ok(response);
    }

    /**
     * Agrega un libro a favoritos
     */
    @PostMapping("/favoritos/agregar")
    @ResponseBody
    public ResponseEntity<?> agregarFavorito(
            @RequestParam("libroId") String libroId,
            @RequestParam("titulo") String titulo,
            @RequestParam("autor") String autor,
            @RequestParam("imagenUrl") String imagenUrl) {

        Map<String, Object> response = new HashMap<>();
        
        try {
            FavoriteBook favorito = libroFavoritoService.addFavorite(libroId, titulo, autor, imagenUrl);
            response.put("mensaje", "Libro agregado a favoritos correctamente");
            response.put("favorito", favorito);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Elimina un libro de favoritos
     */
    @DeleteMapping("/favoritos/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarFavorito(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("Hola como estas");
        try {
            libroFavoritoService.deleteYourEntity(id);
            response.put("mensaje", "Libro eliminado de favoritos correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    /**
     * Obtiene todos los libros favoritos del usuario actual
     */
    @GetMapping("/favoritos/lista")
    @ResponseBody
    public ResponseEntity<?> listaFavoritos() {
        try {
            List<FavoriteBook> favoritos = libroFavoritoService.getActualUserFavoriteBooks();
            return ResponseEntity.ok(favoritos);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}