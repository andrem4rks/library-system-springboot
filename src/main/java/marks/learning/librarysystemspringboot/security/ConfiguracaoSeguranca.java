package marks.learning.librarysystemspringboot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class ConfiguracaoSeguranca {

    @Bean
    public UserDetailsService userDetailsService() throws Exception{
        return null;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http.authorizeRequests()
       .antMatchers("/").permitAll() 
       .antMatchers("/auth/user/*").hasAnyAuthority("USER","ADMIN","BIBLIOTECARIO")
       .antMatchers("/auth/admin/*").hasAnyAuthority("ADMIN")
       .antMatchers("/auth/biblio/*").hasAnyAuthority("BIBLIOTECARIO")
       .antMatchers("/usuario/admin/*").hasAnyAuthority("ADMIN")
       .and()
       .exceptionHandling().accessDeniedPage("/auth/auth-acesso-negado")
       .and()
       .formLogin()
       .loginPage("/publica-index").permitAll()
       .and()
       .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
       .logoutSuccessUrl("/").permitAll();
       return http.build();

    }   
}
    

