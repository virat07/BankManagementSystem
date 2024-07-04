package com.bankingManagementSystem.demo.service.impl;

import com.bankingManagementSystem.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;

public interface UserService {
    @Autowired
     BankResponse createAccount(User user);
     BankResponse balanceEnquiry(EnquiryRequest enquiryRequest);
     String nameEnquiry(EnquiryRequest request);
     BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
     BankResponse debitAccount(CreditDebitRequest debitRequest);
     BankResponse transferRequest(TransferRequest transferRequest);
}
