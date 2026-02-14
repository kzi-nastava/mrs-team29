package service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${app.backend.url:http://localhost:8081}")
    private String backendUrl;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String toEmail, String fullName, String activationToken) {
        // Backend activation endpoint - works for both web and mobile
        String activationLink = backendUrl + "/api/auth/activate?token=" + activationToken;
        
        String subject = "Activate Your Driverr Account";
        String body = "Dear " + fullName + ",\n\n"
                + "Thank you for registering with Driverr! To complete your registration and activate your account, "
                + "please click the link below.\n\n"
                + "Activation Link: " + activationLink + "\n\n"
                + "This link will expire in 24 hours.\n\n"
                + "If you did not create this account, please ignore this email.\n\n"
                + "Best regards,\n"
                + "Driverr Team";

        sendEmail(toEmail, subject, body);
    }

    public void sendPasswordResetEmail(String toEmail, String fullName, String resetToken) {
        // Backend reset endpoint - works for both web and mobile
        String resetLink = backendUrl + "/api/auth/password-reset/reset?token=" + resetToken;
        
        String subject = "Reset Your Driverr Password";
        String body = "Dear " + fullName + ",\n\n"
                + "We received a request to reset your password. To proceed with resetting your password, "
                + "please click the link below.\n\n"
                + "Reset Link: " + resetLink + "\n\n"
                + "This link will expire in 1 hour.\n\n"
                + "If you did not request a password reset, please ignore this email.\n\n"
                + "Best regards,\n"
                + "Driverr Team";

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(mailFrom == null || mailFrom.isBlank() ? "noreply@driverr.com" : mailFrom);

            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
