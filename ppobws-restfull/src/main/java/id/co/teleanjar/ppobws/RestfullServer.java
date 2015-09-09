package id.co.teleanjar.ppobws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * @author moe
 */

@Configuration
@Import({PpobwsApp.class})
public class RestfullServer extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(RestfullServer.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RestfullServer.class);
    }
    
    
    
}
