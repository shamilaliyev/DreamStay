package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import repositories.UserRepository;
import services.AuthService;
import services.EmailVerificationService;
import services.FileUploadService;
import services.IDUploadService;
import services.MailService;
import services.MessageRepository;
import services.PropertyManager;
import services.ReportService;
import services.ReviewService;
import services.SecurityService;
import services.VerificationService;
import repositories.PropertyRepository;

@Configuration
public class AppConfig {

    @Bean
    public SecurityService securityService() {
        return new SecurityService();
    }

    @Bean
    public AuthService authService(UserRepository userRepository, SecurityService securityService) {
        // AuthService now uses the database via UserRepository instead of JSON files
        return new AuthService(userRepository, securityService);
    }

    @Bean
    public PropertyManager propertyManager(PropertyRepository propertyRepository, MessageRepository messageManager) {
        return new PropertyManager(propertyRepository, messageManager);
    }

    @Bean
    public MessageRepository messageManager(repositories.MessageRepository jpaMessageRepository) {
        return new MessageRepository(jpaMessageRepository);
    }

    @Bean
    public MailService mailService(AuthService authService, MessageRepository messageRepository,
            repositories.BlockRepository blockRepository) {
        return new MailService(authService, messageRepository, blockRepository);
    }

    @Bean
    public ReviewService reviewService(AuthService authService, MessageRepository messageManager,
            repositories.ReviewRepository reviewRepository) {
        return new ReviewService(authService, messageManager, reviewRepository);
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
    public ReportService reportService(repositories.ReportRepository reportRepository) {
        return new ReportService(reportRepository);
    }

    @Bean
    public FileUploadService fileUploadService() {
        return new FileUploadService();
    }
}
