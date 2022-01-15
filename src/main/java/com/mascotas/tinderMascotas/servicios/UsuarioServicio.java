package com.mascotas.tinderMascotas.servicios;

import com.mascotas.tinderMascotas.entidades.Foto;
import com.mascotas.tinderMascotas.entidades.Usuario;
import com.mascotas.tinderMascotas.entidades.Zona;
import com.mascotas.tinderMascotas.enumeraciones.Roles;
import com.mascotas.tinderMascotas.errores.ErrorServicio;
import com.mascotas.tinderMascotas.repositorios.UsuarioRepositorio;
import com.mascotas.tinderMascotas.repositorios.ZonaRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio repousuario;

    @Autowired
    private FotoServicio fotoservicio;

//    @Autowired
//    private NotificacionServicio notificacionServicio;
    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String mail, String clave, String clave2, String idzona) throws ErrorServicio {
        Zona zona = zonaRepositorio.getOne(idzona);

        validacion(mail, nombre, apellido, mail, clave, clave2, zona);

        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);
        usuario.setZona(zona);

        usuario.setRoles(Roles.USUARIO);

        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);
//        usuario.setClave(clave);

        usuario.setAlta(new Date());

        Foto foto = fotoservicio.guardar(archivo);
        usuario.setFoto(foto);
        repousuario.save(usuario);

//        notificacionServicio.enviar("Bienvenidos al Tinder de Mascota!!", "Tinder de Mascota", usuario.getMail());
    }

    @Transactional
    public void modificarUsuario(MultipartFile archivo, String id, String nombre, String apellido, String mail, String clave, String clave2, String idzona) throws ErrorServicio {

        Zona zona = zonaRepositorio.getOne(idzona);
        validacion(id, nombre, apellido, mail, clave, clave2, zona);
        Optional<Usuario> op = repousuario.findById(id);
        if (op.isPresent()) {
            Usuario usuario = op.get();

            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);
            usuario.setZona(zona);

            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);
//            usuario.setClave(clave);

            String idfoto = null;
            if (usuario.getFoto() != null) {
                idfoto = usuario.getFoto().getId();
            }
            Foto foto = fotoservicio.actualizar(idfoto, archivo);
            usuario.setFoto(foto);
            repousuario.save(usuario);

        } else {
            throw new ErrorServicio("No se encontro el usuario");
        }
    }

    @Transactional
    public void deshabilitarUsuario(String id) throws ErrorServicio {

        Optional<Usuario> op = repousuario.findById(id);
        if (op.isPresent()) {
            Usuario usuario = op.get();
            usuario.setBaja(new Date());
            repousuario.save(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario");
        }
    }

    @Transactional
    public void habilitarUsuario(String id) throws ErrorServicio {

        Optional<Usuario> op = repousuario.findById(id);
        if (op.isPresent()) {
            Usuario usuario = op.get();
            usuario.setBaja(null);
            repousuario.save(usuario);
        } else {
        }
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(String id) throws ErrorServicio {
        Usuario usuario = repousuario.getById(id);
        if (usuario != null) {
            return usuario;
        } else {
            throw new ErrorServicio("No se encontro el usuario");

        }
    }
    
    
    public void validacion(String id, String nombre, String apellido, String mail, String clave, String clave2, Zona zona) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido no puede ser nulo");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail no puede ser nulo");
        }
        if (clave == null || nombre.isEmpty() || clave.length() < 6) {
            throw new ErrorServicio("El clave no puede ser nula o no puede tener menos de 6 digitos");
        }
        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las claves ingresadas no coinciden");
        }
//        if (repousuario.buscarUsuarioPorMail(mail) != null) {
//            throw new ErrorServicio("El mail ya esta registrado");
//        }
        if (zona == null) {
            throw new ErrorServicio("No se encontro la zona");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = repousuario.buscarUsuarioPorMail(mail);
        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();

//            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_"+ usuario.getRoles());
//            permisos.add(p1);
            GrantedAuthority p2 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
            permisos.add(p2);
//            GrantedAuthority p3 = new SimpleGrantedAuthority("MODULO_VOTOS");
//            permisos.add(p3

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        } else {
            return null;
        }
    }
}
