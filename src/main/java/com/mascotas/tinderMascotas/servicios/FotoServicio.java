package com.mascotas.tinderMascotas.servicios;

import com.mascotas.tinderMascotas.entidades.Foto;
import com.mascotas.tinderMascotas.errores.ErrorServicio;
import com.mascotas.tinderMascotas.repositorios.FotoRepositorio;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FotoServicio {

    @Autowired
    private FotoRepositorio fr;

    @Transactional
    public Foto guardar(MultipartFile archivo) throws ErrorServicio {

        if (archivo != null && !archivo.isEmpty()) {
            try {
                Foto foto = new Foto();
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                return fr.save(foto);
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }
        }
        return null;
    }
    
    @Transactional
        public Foto actualizar(String idfoto, MultipartFile archivo) throws ErrorServicio {
             if (archivo != null) {
            try {
                Foto foto = new Foto();
                
                if (idfoto != null) {
                    Optional<Foto> respuesta= fr.findById(idfoto);
                    if (respuesta.isPresent()) {
                        foto= respuesta.get();
                    }
                }
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                return fr.save(foto);
            } catch (Exception e) {
                System.err.print(e.getMessage());
            }
        }
        return null; 
        }

    
    
}
