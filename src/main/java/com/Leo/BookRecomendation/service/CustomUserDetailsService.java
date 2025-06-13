package com.Leo.BookRecomendation.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.Leo.BookRecomendation.entity.User;
import com.Leo.BookRecomendation.repository.UserRepository;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        System.out.println("Hola");
        if (user == null) {
            throw new UsernameNotFoundException("User was not found with email: " + email);
        }
        return new CustomUserDetails(user);
    }
}
