package es.entreVarales.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class webConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/").setViewName("PaginaRedirectFormularioRegistro"); // sin redirect!
//    }
	
	 @Override
	    public void addInterceptors(InterceptorRegistry registry) {
	        registry.addInterceptor(new AuthInterceptor())
	                .addPathPatterns("/**") // Aplica a todas las rutas
	                .excludePathPatterns("/", "/login", "/register", "/logout", "/css/**", "/js/**", "/images/**");
	    }
}

