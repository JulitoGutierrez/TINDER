package com.mascotas.tinderMascotas.servicios;

import com.mascotas.tinderMascotas.entidades.Mascota;
import com.mascotas.tinderMascotas.entidades.Voto;
import com.mascotas.tinderMascotas.errores.ErrorServicio;
import com.mascotas.tinderMascotas.repositorios.MascotaRepositorio;
import com.mascotas.tinderMascotas.repositorios.VotoRepositorio;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VotoServicio {

    @Autowired
    private VotoRepositorio vr;

    @Autowired
    private MascotaRepositorio mr;

    @Autowired
    private NotificacionServicio notificacionServicio;
    
    @Transactional
    public void votar(String idusuario, String idmascota1, String idmascota2) throws ErrorServicio {
        Voto voto = new Voto();
        voto.setFecha(new Date());

        if (idmascota1.equals(idmascota2)) {
            throw new ErrorServicio("No puede votarse a si mismo");
        }
        Optional<Mascota> respuesta = mr.findById(idmascota1);
        if (respuesta.isPresent()) {
            Mascota mascota1 = respuesta.get();
            if (mascota1.getUsuario().getId().equals(idusuario)) {
                voto.setMascota1(mascota1);
            } else {
                throw new ErrorServicio("No tiene permisos suficientes");
            }
        } else {
            throw new ErrorServicio("No se encontro la mascota");
        }

        Optional<Mascota> respuesta2 = mr.findById(idmascota2);
        if (respuesta2.isPresent()) {
            Mascota mascota2 = respuesta2.get();
            voto.setMascota2(mascota2);

            notificacionServicio.enviar("Tu mascota ha sido votada", "Tinder de Mascotas", mascota2.getUsuario().getMail());
        } else {
            throw new ErrorServicio("No se encontro la mascota");
        }
        vr.save(voto);
    }

    public void responder(String idusuario, String idvoto) throws ErrorServicio {
        Optional<Voto> respuesta = vr.findById(idvoto);
        if (respuesta.isPresent()) {
            Voto voto = respuesta.get();
            voto.setRespuesta(new Date());
            if (voto.getMascota2().getUsuario().getId().equals(idusuario)) {
                notificacionServicio.enviar("Tu mascota ha sido votada", "Tinder de Mascotas", voto.getMascota1().getUsuario().getMail());
                vr.save(voto);
            } else {
                throw new ErrorServicio("No tiene permisos suficientes");
            }
        } else {
            throw new ErrorServicio("No se encontro el voto solicitado");
        }
    }
}
