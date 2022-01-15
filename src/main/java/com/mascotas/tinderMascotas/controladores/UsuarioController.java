package com.mascotas.tinderMascotas.controladores;

import com.mascotas.tinderMascotas.entidades.Usuario;
import com.mascotas.tinderMascotas.entidades.Zona;
import com.mascotas.tinderMascotas.errores.ErrorServicio;
import com.mascotas.tinderMascotas.repositorios.ZonaRepositorio;
import com.mascotas.tinderMascotas.servicios.UsuarioServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @GetMapping("/registro")
    public String registro(ModelMap modelo) {
        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);

        return "registro";
    }

    @PostMapping("/registrar")
    public String registrar(ModelMap modelo, MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave1, @RequestParam String clave2, @RequestParam String idzona) {

        try {
            usuarioServicio.registrar(archivo, nombre, apellido, mail, clave1, clave2, idzona);
        } catch (ErrorServicio ex) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("mail", mail);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);

            return "registro.html";
        }

        modelo.put("titulo", "Bienvenido a Tinder de Mascotas");
        modelo.put("descripcion", "Su usuario fue registrado de manera exitosa");
        return "exito.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editarPerfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap modelo) {
        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);
        
        Usuario login =(Usuario) session.getAttribute("usuariosession");
        if (login == null|| !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        try {
            Usuario usuario = usuarioServicio.buscarPorId(id);
            modelo.addAttribute("perfil", usuario);
        } catch (Exception ex) {
            modelo.addAttribute("error", ex.getMessage());
        }
        return "perfil";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizarPerfil")
    public String actualizarPerfil(ModelMap modelo, MultipartFile archivo, HttpSession session, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave1, @RequestParam String clave2, @RequestParam String idzona) {
        Usuario usuario = null;
        try {
            usuario = usuarioServicio.buscarPorId(id);
            usuarioServicio.modificarUsuario(archivo, id, nombre, apellido, mail, clave2, clave2, idzona);
            session.setAttribute("usuariosession", usuario);
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", usuario);

            return "perfil";
        }
    }

}
