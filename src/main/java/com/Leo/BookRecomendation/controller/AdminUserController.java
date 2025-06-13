package com.Leo.BookRecomendation.controller;

import com.Leo.BookRecomendation.entity.Role;
import com.Leo.BookRecomendation.entity.User;
import com.Leo.BookRecomendation.repository.RoleRepository;
import com.Leo.BookRecomendation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/usuarios")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("usuarios", users);
        return "administrador";
    }


    @GetMapping("/usuarios/{id}")
    public String verUsuario(@PathVariable Long id, Model model) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            model.addAttribute("usuario", userOpt.get());
            model.addAttribute("todosLosRoles", roleRepository.findAll());
            model.addAttribute("tieneImagen", userOpt.get().getImage() != null);
            return "editarUsuario";
        } else {
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/usuarios/update/{id}")
    public String updateUser(@PathVariable Long id,
                                @RequestParam(value = "roles", required = false) List<Long> roleIds,
                                @RequestParam(value = "password", required = false) String password,
                                @RequestParam(value = "email", required = false) String email, 
                                @RequestParam(value = "nombre", required = false) String nombre, 
                                RedirectAttributes redirectAttributes){
        if(id != -1L){
            Optional<User> existentUser = userRepository.findById(id);
            if (existentUser.isPresent()) {
                User existentU = existentUser.get();
                if(email != null){
                    if (!existentU.getEmail().equals(email)) {
                        User existentEmail = userRepository.findByEmail(email);
                        if (existentEmail != null) {
                            redirectAttributes.addFlashAttribute("mensaje", "The email is already used by another user");
                            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                            return "redirect:/admin/usuarios/" + existentU.getID();
                        }
                        existentU.setEmail(email);
                    }
                }
                // Mantener la contraseña si no se proporciona una nueva
                if (!(password == null || password.trim().isEmpty()))
                    existentU.setPassword(passwordEncoder.encode(password));
                Set<Role> roles = new HashSet<>();
                if (roleIds != null && !roleIds.isEmpty()) {
                    for (Long roleId : roleIds) {
                        roleRepository.findById(roleId).ifPresent(roles::add);
                    }
                } else {
                    // Si no se seleccionó ningún rol, asignar ROLE_USER por defecto
                    roleRepository.findByName("ROLE_USER").ifPresent(roles::add);
                }
                existentU.setRoles(roles);
                userRepository.save(existentU);
                
                redirectAttributes.addFlashAttribute("mensaje", "user updated successfully");
                redirectAttributes.addFlashAttribute("tipoMensaje", "succesful");
                
                return "redirect:/admin/usuarios";
            }
            return "redirect:/admin/usuarios";
        }
        User newUser = new User();
        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensaje", "The password is needed");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/usuarios/nuevo";
        }
        User existentEmail = userRepository.findByEmail(email);
        if (existentEmail != null) {
            redirectAttributes.addFlashAttribute("mensaje", "The email is already used by another user");
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/usuarios/nuevo";
        }
        newUser.setEmail(email);
        newUser.setName(nombre);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepository.save(newUser);
        redirectAttributes.addFlashAttribute("mensaje", "User was created successfully");
        redirectAttributes.addFlashAttribute("tipoMensaje", "succesful");
        
        return "redirect:/admin/usuarios";

    }

    @PostMapping("/usuarios/guardar")
    public String saveUsers(@ModelAttribute User user, 
                                 @RequestParam(value = "roles", required = false) List<Long> roleIds,
                                 @RequestParam(value = "password", required = false) String password,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Verificar si es un usuario nuevo o existente
            boolean isNew = (user.getID() == null);
            
            // Si es un usuario existente, obtenerlo para no perder la imagen y verificar cambios en el email
            if (!isNew) {
                Optional<User> existentUser = userRepository.findById(user.getID());
                if (existentUser.isPresent()) {
                    User existentU = existentUser.get();
                    
                    // Verificar si se está intentando cambiar el email a uno ya existente
                    if (!existentU.getEmail().equals(user.getEmail())) {
                        User existentEmail = userRepository.findByEmail(user.getEmail());
                        if (existentEmail != null && !existentEmail.getID().equals(user.getID())) {
                            redirectAttributes.addFlashAttribute("mensaje", "The email is already used by another user");
                            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                            return "redirect:/admin/usuarios/" + user.getID();
                        }
                    }
                    
                    // Mantener la imagen existente
                    user.setImage(existentU.getImage());
                    
                    // Mantener la contraseña si no se proporciona una nueva
                    if (password == null || password.trim().isEmpty()) {
                        user.setPassword(existentU.getPassword());
                    } else {
                        user.setPassword(passwordEncoder.encode(password));
                    }
                }
            } else {
                // Para usuario nuevo, la contraseña es obligatoria
                if (password == null || password.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("mensaje", "The password is needed");
                    redirectAttributes.addFlashAttribute("tipoMensaje", "error");
                    return "redirect:/admin/usuarios/nuevo";
                }
                user.setPassword(passwordEncoder.encode(password));
            }
            
            // Asignar roles
            Set<Role> roles = new HashSet<>();
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Long roleId : roleIds) {
                    roleRepository.findById(roleId).ifPresent(roles::add);
                }
            } else {
                // Si no se seleccionó ningún rol, asignar ROLE_USER por defecto
                roleRepository.findByName("ROLE_USER").ifPresent(roles::add);
            }
            user.setRoles(roles);
            
            // Guardar usuario
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                isNew ? "User was created successfully" : "user updated successfully");
            redirectAttributes.addFlashAttribute("tipoMensaje", "succesful");
            
            return "redirect:/admin/usuarios";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "An error has ocurred: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
            return "redirect:/admin/usuarios";
        }
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model) {
        User aux = new User();
        model.addAttribute("usuario", aux);
        model.addAttribute("todosLosRoles", roleRepository.findAll());
        return "editarUsuario";
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "exito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "error");
        }
        return "redirect:/admin/usuarios";
    }
}