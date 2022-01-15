/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mascotas.tinderMascotas.controladores;

import com.mascotas.tinderMascotas.entidades.Mascota;
import com.mascotas.tinderMascotas.entidades.Usuario;
import com.mascotas.tinderMascotas.errores.ErrorServicio;

import com.mascotas.tinderMascotas.repositorios.VotoRepositorio;
import com.mascotas.tinderMascotas.servicios.MascotaServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
@Controller
@RequestMapping("/voto")
public class VotoControlador {

    @Autowired
    private VotoRepositorio votoRepositorio;
    @Autowired
    private MascotaServicio mascotaServicio;

    @GetMapping("/listaMascotas")
    public String misMascotas(HttpSession session, ModelMap modelo) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/login";
        }

        List<Mascota> mascotas = mascotaServicio.buscarMascotaPorUsuarioZona(login.getZona().getId());
        if (mascotas == null) {
            return "inicio";
        }
        modelo.put("mascotas2", mascotas);
//            return "mascotas2";
        return "Voto.html";

    }
}
