package com.mascotas.tinderMascotas;


import com.mascotas.tinderMascotas.servicios.UsuarioServicio;

import org.springframework.beans.factory.annotation.Autowired;
//import com.mascotas.tinderMascotas.repositorios.ZonaRepositorio;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class WebApplication { 

    @Autowired
    private UsuarioServicio us;
    

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(us)
                .passwordEncoder(new BCryptPasswordEncoder());
    }
    

}
