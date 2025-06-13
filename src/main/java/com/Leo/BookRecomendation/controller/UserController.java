package com.Leo.BookRecomendation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.Leo.BookRecomendation.entity.User;
import com.Leo.BookRecomendation.repository.UserRepository;

@Controller
public class UserController {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/user/register")
    public String registrarUsuario(@ModelAttribute User usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }
}
