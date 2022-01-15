
package com.mascotas.tinderMascotas.controladores;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/")
public class PortalControlador {
  
     @GetMapping("/")
    public String index() {
        return "index";
    }
    
     @GetMapping("login2")
    public String login(@RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {
         if (error != null) {
             model.put("error", "Nombre de usuario o clave incorrecta");
         }
         if (logout != null) {
             model.put("logout", "Ha salido correctamente!");
         }
        return "login2";
    }
    
    @GetMapping("exito")
    public String exito() {
        return "exito";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("inicio")
    public String inicio() {
        return "inicio";
    }
 
    @GetMapping("perfil")
    public String perfil() {
        return "perfil.html";
    }
}
