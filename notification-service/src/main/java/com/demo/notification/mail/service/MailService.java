package com.demo.notification.mail.service;

import com.demo.notification.mail.exception.MailException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class MailService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private Environment environment;

  public void sendMail(String destinyMail, String replyTo, String subject, String body) throws MessagingException {

    Properties props = System.getProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.port", environment.getProperty("mail.smtpPort"));
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.auth", "true");

    Session session = Session.getDefaultInstance(props);

    Transport transport = null;

    try {
      transport = session.getTransport();

      MimeMessage msg = new MimeMessage(session);

      msg.setFrom(new InternetAddress(environment.getProperty("mail.companyEmail"), environment.getProperty("mail.companyName")));
      msg.setRecipient(Message.RecipientType.TO, new InternetAddress(destinyMail));
      msg.setSubject(subject, "UTF-8");
      msg.setContent(body, "text/html");
      msg.setReplyTo(InternetAddress.parse(replyTo, false));
      msg.setSentDate(new Date());

      transport.connect(environment.getProperty("mail.smtpHost"),
                        environment.getProperty("mail.smtpUser"),
                        environment.getProperty("mail.smtpPassword"));
      transport.sendMessage(msg, msg.getAllRecipients());

    } catch (Exception e) {
      logger.error("Could not send mail to {} due to {}", destinyMail, e.getMessage());
      throw new MailException("Could not send mail to " + destinyMail, e);
    } finally {
      transport.close();
    }
  }
}