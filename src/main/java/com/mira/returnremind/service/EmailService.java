package com.mira.returnremind.service;

import com.mira.returnremind.model.Notification;
import com.mira.returnremind.model.NotificationType;
import com.mira.returnremind.model.Purchase;
import com.mira.returnremind.model.User;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${returnremind.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${sendgrid.api.key:}")
    private String sendGridApiKey;

    @Value("${returnremind.email.from:noreply@returnremind.com}")
    private String fromAddress;

    public void sendNotificationEmail(Notification notification) {
        Purchase purchase = notification.getPurchase();
        User user = purchase.getUser();

        String subject = buildSubject(notification);
        String body = buildBody(notification);

        if (emailEnabled) {
            sendEmail(user.getEmail(), subject, body);
        } else {
            logEmail(user.getEmail(), subject, body);
        }
    }

    private String buildSubject(Notification notification) {
        Purchase purchase = notification.getPurchase();
        NotificationType type = notification.getType();

        return switch (type) {
            case SEVEN_DAYS_BEFORE -> "7 days left to return: " + purchase.getItemName();
            case ONE_DAY_BEFORE -> "Last day tomorrow: " + purchase.getItemName();
            case DEADLINE_REACHED -> "Return deadline TODAY: " + purchase.getItemName();
        };
    }

    private String buildBody(Notification notification) {
        Purchase purchase = notification.getPurchase();
        User user = purchase.getUser();
        NotificationType type = notification.getType();

        StringBuilder body = new StringBuilder();
        body.append("Hi ").append(user.getName()).append(",\n\n");

        switch (type) {
            case SEVEN_DAYS_BEFORE -> {
                body.append("This is a friendly reminder that you have 7 days left ");
                body.append("to return your purchase.\n\n");
            }
            case ONE_DAY_BEFORE -> {
                body.append("Your return window closes TOMORROW! ");
                body.append("Don't forget to return your item if needed.\n\n");
            }
            case DEADLINE_REACHED -> {
                body.append("TODAY is the last day to return your purchase. ");
                body.append("Act now if you need to make a return!\n\n");
            }
        }

        body.append("Purchase Details:\n");
        body.append("----------------------------\n");
        body.append("Item: ").append(purchase.getItemName()).append("\n");
        body.append("Merchant: ").append(purchase.getMerchantName()).append("\n");
        body.append("Purchased: ").append(purchase.getPurchaseDate()).append("\n");
        body.append("Return Deadline: ").append(purchase.getReturnDeadline()).append("\n");
        body.append("----------------------------\n\n");

        body.append("Happy shopping!\n");
        body.append("- ReturnRemind\n");

        return body.toString();
    }

    private void sendEmail(String to, String subject, String body) {
        Email from = new Email(fromAddress);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("[EMAIL SENT] To: {} | Subject: {}", to, subject);
            } else {
                log.error("[EMAIL FAILED] Status: {} | Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("SendGrid returned status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            log.error("[EMAIL FAILED] To: {} | Error: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private void logEmail(String to, String subject, String body) {
        log.info("\n[MOCK EMAIL - NOT SENT]\n" +
                "To: {}\n" +
                "Subject: {}\n" +
                "Body:\n{}", to, subject, body);
    }
}