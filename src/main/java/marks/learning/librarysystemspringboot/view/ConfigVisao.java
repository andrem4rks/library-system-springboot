package marks.learning.librarysystemspringboot.view;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class ConfigVisao implements WebMvcConfigurer {
    
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/auth/auth-acesso-negado").setViewName("/auth/auth-acesso-negado");
    }
}
