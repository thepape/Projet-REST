package org.m2acsi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
public class ResourceServerConfig {
    @Configuration
    @EnableResourceServer
    protected static class ServerConfig extends ResourceServerConfigurerAdapter {
        
        @Autowired
        private CustomAuthenticationEntryPoint myEntryPoint;
        
        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.exceptionHandling()
                    .authenticationEntryPoint(myEntryPoint)	//pas obligatoire
                    .and()
                    .authorizeRequests()
                    .anyRequest().permitAll();	//pour le moment
                    /*
                    .antMatchers(HttpMethod.POST, "/demandes").permitAll()
                    .anyRequest().authenticated();
                    */
        }
    }
    
}
