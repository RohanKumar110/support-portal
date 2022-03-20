package dev.rohankumar.supportportal.service.impl;

import com.sun.mail.smtp.SMTPTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import static dev.rohankumar.supportportal.constant.EmailConstant.*;

@Service
public class EmailService {

    @Value("${smtp.username}")
    private String smtpUsername;
    @Value("${smtp.password}")
    private String smtpPassword;

    public void sendNewPasswordEmail(String firstName,String password,String email)  {
       try{
           Message message = createEmail(firstName,password,email);
           SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
           smtpTransport.connect(GMAIL_SMTP_SERVER,smtpUsername,smtpPassword);
           smtpTransport.sendMessage(message,message.getAllRecipients());
           smtpTransport.close();
       }catch (MessagingException ex){
           ex.printStackTrace();
       }
    }

    private Session getEmailSession(){
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST,GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH,true);
        properties.put(SMTP_PORT,DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE,true);
        properties.put(SMTP_STARTTLS_REQUIRED,true);
        return Session.getInstance(properties,null);
    }

    private Message createEmail(String firstName,String password,String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIl));
        message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email,false));
        message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(CC_EMAIL,false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText(getFormattedMessage(firstName,password));
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }

    private String getFormattedMessage(String firstName, String password) {

        String message = "Hello %s \n \nYour new account password is: %s \n \n  The Support Team";
        return String.format(message,firstName,password);
    }
}
