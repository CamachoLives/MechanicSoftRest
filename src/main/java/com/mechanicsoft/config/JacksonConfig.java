package com.mechanicsoft.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    /**
     * Sin esto, Jackson no sabe serializar los proxies LAZY de Hibernate
     * (@ManyToOne/@ManyToMany) y falla con "ByteBuddyInterceptor" en
     * cualquier endpoint que devuelva una entidad con relaciones.
     */
    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();
        module.enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        return module;
    }
}
