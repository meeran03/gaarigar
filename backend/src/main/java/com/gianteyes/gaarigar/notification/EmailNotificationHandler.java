package com.gianteyes.gaarigar.notification;

import java.io.UnsupportedEncodingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.core.env.Environment;
import org.thymeleaf.context.Context;
@Component
public class EmailNotificationHandler implements NotificationHandler {

    protected final Environment environment;
    protected  final JavaMailSender mailSender;
    protected  final TemplateEngine htmlTemplateEngine;

    public EmailNotificationHandler(Environment environment, JavaMailSender mailSender, TemplateEngine htmlTemplateEngine) {
        this.environment = environment;
        this.mailSender = mailSender;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }
    public void setContextForOrderPlacedTemplate(Context ctx,Note note)
    {
        ctx.setVariable("subject", note.getSubject());
        ctx.setVariable("content", note.getContent());
        ctx.setVariable("name", note.getUser().getFirstName()+" "+note.getUser().getLastName());
        ctx.setVariable("orderId", note.getData().get("id"));
        ctx.setVariable("orderType",note.getData().get("type"));
        ctx.setVariable("paymentMethod",note.getData().get("paymentMethod"));
    }
    public final String send(Note note) throws MessagingException, UnsupportedEncodingException {
       final String LOGO_IMAGE = "static/images/logo.png";
        final String PNG_MIME = "image/png";
        String mailFrom = environment.getProperty("spring.mail.properties.mail.smtp.from");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper email = new MimeMessageHelper(mimeMessage,true,"UTF-8");

        email.setTo(note.getData().get("email"));
        email.setSubject(note.getMailSubject());
        email.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("Logo", LOGO_IMAGE);
        setContextForOrderPlacedTemplate(ctx,note);

        final String htmlContent = this.htmlTemplateEngine.process(note.getTemplateName(), ctx);

        email.setText(htmlContent, true);

      ClassPathResource clr = new ClassPathResource(LOGO_IMAGE);

        email.addInline("Logo", clr, PNG_MIME);

       mailSender.send(mimeMessage);
       String message = "Email send successfully";
        return message;
    }
}
