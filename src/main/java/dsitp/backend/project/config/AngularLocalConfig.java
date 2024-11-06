package dsitp.backend.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Profile("local")
//public class AngularLocalConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//
//            @Override
//            public void addCorsMappings(final CorsRegistry registry) {
//                registry.addMapping("/**").allowedMethods("*").allowedOrigins("http://localhost:4200");
//            }
//
//        };
//    }
//
//}
@Configuration
public class AngularLocalConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Permite CORS en todas las rutas que comiencen con "/api"
                .allowedOrigins("http://localhost:4200") // Permite solicitudes desde el puerto de Angular (usualmente 4200)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Métodos permitidos
                .allowCredentials(true);
    }
}
