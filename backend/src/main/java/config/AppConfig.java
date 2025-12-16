package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.*;

@Configuration
public class AppConfig {

    @Bean
    public SecurityService securityService() {
        return new SecurityService();
    }

    @Bean
    public AuthService authService(SecurityService securityService) {
        return new AuthService(securityService);
    }

    @Bean
    public PropertyManager propertyManager() {
        return new PropertyManager();
    }

    @Bean
    public MessageRepository messageRepository() {
        return new MessageRepository();
    }

    @Bean
    public MailService mailService(AuthService authService, MessageRepository messageRepository) {
        return new MailService(authService, messageRepository);
    }

    @Bean
    public ReviewService reviewService(AuthService authService, MessageRepository messageRepository) {
        return new ReviewService(authService, messageRepository);
    }

    @Bean
    public VerificationService verificationService(AuthService authService, PropertyManager propertyManager) {
        return new VerificationService(authService, propertyManager);
    }

    @Bean
    public EmailVerificationService emailVerificationService(SecurityService securityService) {
        return new EmailVerificationService(securityService);
    }

    @Bean
    public IDUploadService idUploadService() {
        return new IDUploadService();
    }

    @Bean
    public ReportService reportService() {
        return new ReportService();
    }

    @Bean
    public FileUploadService fileUploadService() {
        return new FileUploadService();
    }
}
