package service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String toEmail, String fullName, String activationToken) {
        String activationLink = "http://localhost:4200/activate?token=" + activationToken;
        
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
        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;
        
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
            message.setFrom("noreply@driverr.com");

            mailSender.send(message);
            System.out.println("Email sent successfully to " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
