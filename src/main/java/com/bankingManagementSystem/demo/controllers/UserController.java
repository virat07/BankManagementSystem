package com.bankingManagementSystem.demo.controllers;

import com.bankingManagementSystem.demo.model.*;
import com.bankingManagementSystem.demo.repository.UserRepository;
import com.bankingManagementSystem.demo.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping
    public BankResponse createAccount(@RequestBody User userRequest){
        return userService.createAccount(userRequest);
    }
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest enquiryRequest){
        return userService.balanceEnquiry(enquiryRequest);
    }
    @GetMapping("/nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }
    @PostMapping("/credit")
    public  BankResponse creditAccount (@RequestBody CreditDebitRequest request){
        return  userService.creditAccount(request);
    }
    @PostMapping("/debit")
    public  BankResponse debitAccount (@RequestBody CreditDebitRequest request){
        return  userService.debitAccount(request);
    }
    @PostMapping("/transfer")
    public  BankResponse debitAccount (@RequestBody TransferRequest request){
        return  userService.transferRequest(request);
    }
}
