package com.shop.util;

import java.io.InputStream;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailUtil {
    private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);
    private static String fromEmail;
    private static String username;
    private static String password;
    private static String host;
    private static String port;
    private static boolean configured = false;

    static {
        try (InputStream is = EmailUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                host = props.getProperty("smtp.host", "smtp.gmail.com");
                port = props.getProperty("smtp.port", "587");
                username = props.getProperty("smtp.username", "");
                password = props.getProperty("smtp.password", "");
                fromEmail = props.getProperty("smtp.from", username);
                configured = username != null && !username.isEmpty() && !"your-email@gmail.com".equals(username);
            }
        } catch (Exception e) {
            log.warn("Could not load email config from db.properties", e);
        }
    }

    public static void sendOrderConfirmation(String toEmail, String subject, String textBody, String invoiceFilePath) {
        if (!configured) {
            log.info("Email not configured. Would send to: {}, subject: {}, body: {}", toEmail, subject, textBody);
            return;
        }
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        Session session = Session.getInstance(props, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(textBody);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            if (invoiceFilePath != null && new File(invoiceFilePath).exists()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                attachmentPart.attachFile(new File(invoiceFilePath));
                multipart.addBodyPart(attachmentPart);
            }
            message.setContent(multipart);
            Transport.send(message);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}", toEmail, e);
        }
    }

    public static boolean isConfigured() { return configured; }
}
