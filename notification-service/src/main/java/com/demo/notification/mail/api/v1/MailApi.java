package com.demo.notification.mail.api.v1;

import com.demo.notification.mail.api.v1.dto.MailDto;
import com.demo.notification.mail.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/mail")
public class MailApi {

  private MailService mailService;

  public MailApi(
      MailService mailService) {
    this.mailService = mailService;
  }

  @PostMapping()
  public ResponseEntity<?> sendEmail(
      @RequestBody MailDto destiny) {

    try {
      mailService.sendMail(
          destiny.getDestiny(), destiny.getReplyTo(), destiny.getSubject(), destiny.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.ok().build();
  }
}
