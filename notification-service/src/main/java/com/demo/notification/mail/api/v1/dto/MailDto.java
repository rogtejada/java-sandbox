package com.demo.notification.mail.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {

  private String destiny;
  private String subject;
  private String replyTo;
  private String message;

}
