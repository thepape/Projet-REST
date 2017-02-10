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
        		.and()
        		.authorizeRequests()
        			.antMatchers("/user/**").authenticated();
        }
    }
    
    private static class OAuthCitizenRequestMatcher implements RequestMatcher {

		@Override
		public boolean matches(HttpServletRequest request) {
			String token = request.getHeader("Citizen-token");
			String method = request.getMethod();
			
			String URI = request.getRequestURI();
			boolean correctURIFormat = URI.matches("/demandes/[a-z0-9]+");
			
			boolean isCitizen = (token != null 
					&& (method.equals("GET") || method.equals("PUT"))
					&& correctURIFormat);
			
			return isCitizen;
		}
    	
    }
    
}
