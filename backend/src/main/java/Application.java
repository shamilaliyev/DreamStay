import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = { "services", "models", "controllers", "config", "dtos", "repositories" })
@EnableJpaRepositories(basePackages = "repositories")
@EntityScan(basePackages = "models")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
