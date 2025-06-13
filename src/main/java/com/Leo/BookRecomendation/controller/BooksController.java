package com.Leo.BookRecomendation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Leo.BookRecomendation.entity.FavoriteBook;
import com.Leo.BookRecomendation.entity.User;
import com.Leo.BookRecomendation.repository.UserRepository;
import com.Leo.BookRecomendation.service.FavoriteBookService;

@Controller
public class BooksController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FavoriteBookService favoriteBookService;

    @GetMapping("/libros")
    public String libros() {
        return "libros";  
    }

    @GetMapping("/favoritos")
    public String favorites(Model model){
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        // Buscar el usuario en la base de datos
        User user = userRepository.findByName(username);
        if (user != null) {
            // Añadir los datos del usuario al modelo
            List<FavoriteBook> list = favoriteBookService.getActualUserFavoriteBooks();
            model.addAttribute("favoritos", list);
        }
        
        return "favoritos";
    }

}