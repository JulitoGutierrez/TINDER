package com.mascotas.tinderMascotas.controladores;

import com.mascotas.tinderMascotas.entidades.Mascota;
import com.mascotas.tinderMascotas.entidades.Usuario;
import com.mascotas.tinderMascotas.entidades.Zona;
import com.mascotas.tinderMascotas.enumeraciones.Sexo;
import com.mascotas.tinderMascotas.enumeraciones.Tipo;
import com.mascotas.tinderMascotas.errores.ErrorServicio;
import com.mascotas.tinderMascotas.repositorios.ZonaRepositorio;
import com.mascotas.tinderMascotas.servicios.MascotaServicio;
import com.mascotas.tinderMascotas.servicios.UsuarioServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
@Controller
@RequestMapping("/mascota")
public class MascotaController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private MascotaServicio mascotaServicio;

    @PostMapping("/eliminarPerfil")
    public String eliminar(HttpSession session, @RequestParam String id) {
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            mascotaServicio.eliminarMascota(login.getId(), id);
        } catch (ErrorServicio ex) {
            Logger.getLogger(MascotaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "redirect:/mascota/misMascotas";
    }

    @GetMapping("/misMascotas")
    public String misMascotas(HttpSession session, ModelMap modelo) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/login";
        }

        List<Mascota> mascotas = mascotaServicio.buscarMascotaPorUsuario(login.getId());
        modelo.put("mascotas", mascotas);
        return "mascotas";
    }

    @GetMapping("/editarPerfil")
    public String editarperfil(HttpSession session, @RequestParam(required = false) String id, @RequestParam(required = false) String accion, ModelMap modelo) {

       
        if (accion == null) {
            accion = "Crear";
        }

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/login";
        }
        Mascota mascota = new Mascota();
        if (id != null && !id.isEmpty()) {
            try {
                mascota = mascotaServicio.buscarPorId(id);
            } catch (ErrorServicio ex) {
                Logger.getLogger(MascotaController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        modelo.put("perfil", mascota);
        modelo.put("accion", accion);
        modelo.put("sexos", Sexo.values());
        modelo.put("tipos", Tipo.values());

        return "mascota.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizarPerfil")
    public String actualizarPerfil(ModelMap modelo, MultipartFile archivo, HttpSession session, @RequestParam String id, @RequestParam String nombre, @RequestParam Sexo sexo, @RequestParam Tipo tipo) {

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/login";
        }

        try {
            if (id == null || id.isEmpty()) {
                mascotaServicio.agregarMascota(archivo, login.getId(), nombre, sexo, tipo);
            } else {
                mascotaServicio.modificarMascota(archivo, login.getId(), id, nombre, sexo, tipo);
            }
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {

            Mascota mascota = new Mascota();
            mascota.setId(id);
            mascota.setNombre(nombre);
            mascota.setSexo(sexo);
            mascota.setTipo(tipo);

            modelo.put("accion", "Actualizar");
            modelo.put("sexos", Sexo.values());
            modelo.put("tipos", Tipo.values());
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", login);
            return "mascota.html";
        }
    }

}
