package com.mascotas.tinderMascotas.servicios;

import com.mascotas.tinderMascotas.entidades.Foto;
import com.mascotas.tinderMascotas.entidades.Mascota;
import com.mascotas.tinderMascotas.entidades.Usuario;
import com.mascotas.tinderMascotas.enumeraciones.Sexo;
import com.mascotas.tinderMascotas.enumeraciones.Tipo;
import com.mascotas.tinderMascotas.errores.ErrorServicio;
import com.mascotas.tinderMascotas.repositorios.MascotaRepositorio;
import com.mascotas.tinderMascotas.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MascotaServicio {

    @Autowired
    private UsuarioRepositorio ur;
    @Autowired
    private MascotaRepositorio mr;
    @Autowired
    private FotoServicio fotoservicio;
    
    @Transactional
    public void agregarMascota(MultipartFile archivo, String idusuario, String nombre, Sexo sexo, Tipo tipo) throws ErrorServicio {
        Usuario usuario = ur.findById(idusuario).get();
        validar(nombre, sexo);
        Mascota mascota = new Mascota();
        mascota.setNombre(nombre);
        mascota.setSexo(sexo);
        mascota.setAlta(new Date());
        mascota.setUsuario(usuario);
        mascota.setTipo(tipo);
        
        Foto foto = fotoservicio.guardar(archivo);
        mascota.setFoto(foto);
        mr.save(mascota);
    }

    @Transactional
    public void modificarMascota(MultipartFile archivo, String idusuario, String idmascota, String nombre, Sexo sexo, Tipo tipo) throws ErrorServicio {
        validar(nombre, sexo);
        Optional<Mascota> opmascota = mr.findById(idmascota);
        if (opmascota.isPresent()) {
            Mascota mascota = opmascota.get();
            if (mascota.getUsuario().getId().equals(idusuario)) {
                mascota.setNombre(nombre);
                mascota.setSexo(sexo);
                
                String idfoto = null;
            if (mascota.getFoto()!= null) {
                idfoto= mascota.getFoto().getId();
            }
            Foto foto = fotoservicio.actualizar(idfoto, archivo);
            mascota.setFoto(foto);
            mascota.setTipo(tipo);
                mr.save(mascota);
            } else {
                throw new ErrorServicio("No tiene permisos suficientes");
            }
        } else {
            throw new ErrorServicio("No se encontro la mascota");
        }
    }
    
    @Transactional
    public void eliminarMascota(String idusuario, String idmascota) throws ErrorServicio{
        Optional<Mascota> opmascota = mr.findById(idmascota);
        if (opmascota.isPresent()) {
            Mascota mascota = opmascota.get();
            if (mascota.getUsuario().getId().equals(idusuario)) {
                mascota.setBaja(new Date());
                mr.save(mascota);
            } else {
                throw new ErrorServicio("No tiene permisos suficientes");
            }
        } else {
            throw new ErrorServicio("No se encontro la mascota");
        }
    }
    
    @Transactional(readOnly = true)
    public Mascota buscarPorId(String id) throws ErrorServicio {
        Optional<Mascota> opmascota = mr.findById(id);
        if (opmascota.isPresent()) {
            return opmascota.get();
        } else {
            throw new ErrorServicio("No se encontro la mascota solicitada");

        }
    }

    @Transactional(readOnly = true)
    public void validar(String nombre, Sexo sexo) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre no puede ser nulo");
        }
        if (sexo == null) {
            throw new ErrorServicio("El sexo no puede ser nulo");
        }
    }
    
    public List<Mascota> buscarMascotaPorUsuario(String id){
        return mr.buscarMacotasPorUsuario(id);
    }
    
    public List<Mascota> buscarMascotaPorUsuarioZona(String id){
        
        return mr.buscarMacotasPorUsuarioZona(id);
    }
}
