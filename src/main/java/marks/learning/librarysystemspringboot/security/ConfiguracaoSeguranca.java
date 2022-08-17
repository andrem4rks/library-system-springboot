package marks.learning.librarysystemspringboot.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import marks.learning.librarysystemspringboot.repository.usuarioRepository;

@Configuration
@EnableWebSecurity
public class ConfiguracaoSeguranca extends WebSecurityConfigurerAdapter {

    @Autowired
    private usuarioRepository usuarioRepository;

    @Autowired
    private LoginSucesso loginSucesso;

    @Bean
    public BCryptPasswordEncoder gerarCriptografia() {
        BCryptPasswordEncoder criptografia = new BCryptPasswordEncoder();
        return criptografia;
    }    

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception{
        DetalheUsuarioServico detalheUsuario = new DetalheUsuarioServico(usuarioRepository);
        return detalheUsuario;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.authorizeRequests()
       .antMatchers("/").permitAll() 
       .antMatchers("/auth/user/*").hasAnyAuthority("USER","ADMIN","BIBLIOTECARIO")
       .antMatchers("/auth/admin/*").hasAnyAuthority("ADMIN")
       .antMatchers("/auth/biblio/*").hasAnyAuthority("BIBLIOTECARIO")
       .antMatchers("/usuario/admin/*").hasAnyAuthority("ADMIN")
       .and()
       .exceptionHandling().accessDeniedPage("/resources/auth/auth-acesso-negado")
       .and()
       .formLogin().successHandler(loginSucesso)
       .loginPage("/publica-index").permitAll()
       .and()
       .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
       .logoutSuccessUrl("/").permitAll();
    }
    
    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// O objeto que vai obter os detalhes do usuário
		UserDetailsService detalheDoUsuario = userDetailsService();
		// Objeto para criptografia
		BCryptPasswordEncoder criptografia = gerarCriptografia();
		auth.userDetailsService(detalheDoUsuario).passwordEncoder(criptografia);
	}



}
    

