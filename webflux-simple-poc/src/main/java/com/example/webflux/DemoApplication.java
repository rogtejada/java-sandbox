package com.example.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class DemoApplication {

  static {
    BlockHound.install(
        builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID")
            .allowBlockingCallsInside("java.io.FilterInputStream", "read")
            .allowBlockingCallsInside("java.io.InputStream", "readNBytes")
                      );
  }

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

}
