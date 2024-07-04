package com.bankingManagementSystem.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@OpenAPIDefinition(
       info = @Info(
               title = "Bank Management App",
               description = "Backend management for the bank system",
               version = "1.0",
               contact = @Contact(
                       name="Bharat Gupta",
                       email = "bharatguptawork07@gmail.com"

               )
       )
)
public class BankManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankManagementSystemApplication.class, args);
    }

}
