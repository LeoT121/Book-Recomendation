package com.Leo.BookRecomendation.service;

import com.Leo.BookRecomendation.entity.FavoriteBook;
import com.Leo.BookRecomendation.entity.User;
import com.Leo.BookRecomendation.repository.FavoriteBookRepository;
import com.Leo.BookRecomendation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteBookService {

    @Autowired
    private FavoriteBookRepository favoriteBookRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtiene el usuario actual autenticado
     */
    public User getActualUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByName(username);
    }

    /**
     * Obtiene todos los libros favoritos del usuario actual
     */
    public List<FavoriteBook> getActualUserFavoriteBooks() {
        User user = getActualUser();
        if (user == null) {
            throw new RuntimeException("Usuario was not found");
        }
        return favoriteBookRepository.findByUserOrderByDateDesc(user);
    }

    /**
     * Agrega un libro a favoritos
     */
    @Transactional
    public FavoriteBook addFavorite(String bookId, String title, String author, String imageUrl) {
        User user = getActualUser();
        if (user == null) {
            throw new RuntimeException("User was not found");
        }

        // Verifica si ya existe el favorito
        if (favoriteBookRepository.existsByUserAndBookId(user, bookId)) {
            throw new RuntimeException("The book was already added to favorites");
        }

        // Crea y guarda el nuevo favorito
        FavoriteBook favorite = new FavoriteBook(user, bookId, title, author, imageUrl);
        return favoriteBookRepository.save(favorite);
    }

    /**
     * Elimina un libro de favoritos
     */
    @Transactional
    public void deleteFavorite(Long Id) {
        User user = getActualUser();
        if (user == null) {
            throw new RuntimeException("Usuario was not found");
        }
        System.out.println("Hola como estas");
        favoriteBookRepository.findByUserAndId(user, Id)
                .ifPresent(favorite -> favoriteBookRepository.delete(favorite));
    }

    public void deleteYourEntity(Long id) {
        if (!favoriteBookRepository.existsById(id)) {
            throw new RuntimeException("No se encontró el libro favorito con ID: " + id);
        }
        favoriteBookRepository.deleteById(id);
    }

    /**
     * Verifica si un libro está en favoritos
     */
    public boolean isFavorite(String bookId) {
        User user = getActualUser();
        if (user == null) {
            return false;
        }
        return favoriteBookRepository.existsByUserAndBookId(user, bookId);
    }
}