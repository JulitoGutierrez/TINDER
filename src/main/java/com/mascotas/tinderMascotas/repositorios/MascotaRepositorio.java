/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mascotas.tinderMascotas.repositorios;

import com.mascotas.tinderMascotas.entidades.Mascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepositorio extends JpaRepository<Mascota, String>{

@Query("SELECT c FROM Mascota c WHERE c.usuario.id = :id AND c.baja IS NULL")
public List<Mascota> buscarMacotasPorUsuario(@Param("id") String id);

@Query("SELECT c FROM Mascota c WHERE c.usuario.zona.id = :id ")
public List<Mascota> buscarMacotasPorUsuarioZona(@Param("id") String id);
    
}
