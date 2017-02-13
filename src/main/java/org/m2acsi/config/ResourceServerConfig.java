package org.m2acsi.config;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class ResourceServerConfig {
	
	@EnableResourceServer
    @Configuration
    protected static class CitizenServerConfig extends ResourceServerConfigurerAdapter {
        
        @Autowired
        private CustomAuthenticationEntryPoint myEntryPoint;
        
        @Override
        public void configure(HttpSecurity http) throws Exception {
        	/*
            http.exceptionHandling()
                    .authenticationEntryPoint(myEntryPoint)	//pas obligatoire
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/demandes").permitAll()			//tous le monde peut poster une demande
                    .requestMatchers(new OAuthCitizenRequestMatcher()).permitAll()	//les citoyens peuvent avoir acces a leur demande
                    .anyRequest().authenticated();									//pour tout le reste, il y a mastercard
        	
        	*/
        	
        	http
        		.authorizeRequests()
        			.antMatchers(HttpMethod.POST, "/citizen/demandes").permitAll()
        			.antMatchers(HttpMethod.GET, "/citizen/demandes/{id}").permitAll()
        			.antMatchers(HttpMethod.PUT, "/citizen/demandes/{id}").permitAll()
        			.antMatchers(HttpMethod.GET, "/internal/**").authenticated()
        			.antMatchers(HttpMethod.DELETE, "/internal/demandes/{id}").hasAuthority("perm_delete_demande")
        			.antMatchers(HttpMethod.POST, "/internal/demandes/{id}/actions").authenticated();
        }
    }
    
}
