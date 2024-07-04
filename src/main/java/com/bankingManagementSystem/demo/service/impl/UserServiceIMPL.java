package com.bankingManagementSystem.demo.service.impl;

import com.bankingManagementSystem.demo.model.*;
import com.bankingManagementSystem.demo.repository.UserRepository;
import com.bankingManagementSystem.demo.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class UserServiceIMPL implements UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailService emailService;
    @Override
    public BankResponse createAccount(User userRequest){
        /*
        * Creating an account
        * Check if the user exists or not
        * */
        if(userRepository.existsByEmail(userRequest.getEmail())){
          return   BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        if(userRepository.existsByAccountNumber(userRequest.getAccountNumber())){
            return   BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder().firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(userRequest.getAccountBalance())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();
        User savedUser = userRepository.save(newUser);
        //send email alert
        EmailDetails emailDetails= EmailDetails.builder()
                .recipient(userRequest.getEmail())
                .subject("Account Created")
                .messageBody("Congratulations! Your account has been successfully created. \n" +
                        "Your Account details: \n Account Name: "+savedUser.getFirstName()+ " " + savedUser.getLastName()+"\n Account Number: "+savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return  BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                    .accountBalance(savedUser.getAccountBalance())
                    .accountNumber(savedUser.getAccountNumber())
                    .accountName(savedUser.getFirstName()+" "+savedUser.getLastName())
                    .build())
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest enquiryRequest) {
        // check is the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder().responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE).responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE).accountInfo(null).build();
        }
        User foundUser = userRepository.findByAccountNumber(enquiryRequest.getAccountNumber());
        return  BankResponse.builder().responseCode(AccountUtils.ACCOUNT_FOUND_CODE).responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .accountNumber(enquiryRequest.getAccountNumber())
                .build()).build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExist){
           return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return  foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
        // checking if the account exists;
        boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder().responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE).responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE).accountInfo(null).build();
        }
        User userCredit = userRepository.findByAccountNumber(creditDebitRequest.getAccountNumber());
        userCredit.setAccountBalance(userCredit.getAccountBalance().add(creditDebitRequest.getAmount()));
        userRepository.save(userCredit);
        return BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountName(userCredit.getFirstName() + " " + userCredit.getLastName())
                        .accountBalance(userCredit.getAccountBalance())
                        .accountNumber(creditDebitRequest.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest debitRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(debitRequest.getAccountNumber());
        if(!isAccountExist){
            return BankResponse.builder().responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE).responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE).accountInfo(null).build();
        }
        User userDebit = userRepository.findByAccountNumber(debitRequest.getAccountNumber());
        // balance sufficient or not
        BigInteger availableBalance = userDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = debitRequest.getAmount().toBigInteger();
        if(availableBalance.intValue()<debitAmount.intValue()){
            return BankResponse.builder().responseMessage(AccountUtils.ACCOUNT_NOT_SUFFICIENT_BALANCE_MESSAGE).responseCode(AccountUtils.ACCOUNT_NOT_SUFFICIENT_BALANCE_CODE)  .accountInfo(AccountInfo.builder()
                    .accountName(userDebit.getFirstName() + " " + userDebit.getLastName())
                    .accountBalance(userDebit.getAccountBalance())
                    .accountNumber(debitRequest.getAccountNumber())
                    .build()).build();
        }
        // if sufficient balance then debit it from the account and save it in db
        userDebit.setAccountBalance(userDebit.getAccountBalance().subtract(debitRequest.getAmount()));
        userRepository.save(userDebit);
        //success message
        return BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountName(userDebit.getFirstName() + " " + userDebit.getLastName())
                        .accountBalance(userDebit.getAccountBalance())
                        .accountNumber(debitRequest.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse transferRequest(TransferRequest transferRequest) {
        // get the amount to debit
        // check the amount in the bank
        boolean isAccountExist = userRepository.existsByAccountNumber(transferRequest.getSourceAccount());
        if(!isAccountExist){
            return BankResponse.builder().responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE).responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE).accountInfo(null).build();
        }
        boolean isDestinationAccountExit = userRepository.existsByAccountNumber(transferRequest.getDestinationAccount());
        if(!isDestinationAccountExit){
            return BankResponse.builder().responseMessage(AccountUtils.DESTINATION_ACCOUNT_NOT_EXIST_MESSAGE).responseCode(AccountUtils.DESTINATION_ACCOUNT_NOT_EXISTS_CODE).accountInfo(null).build();
        }
        User userDebit = userRepository.findByAccountNumber(transferRequest.getSourceAccount());
        // balance sufficient or not
        BigInteger availableBalance = userDebit.getAccountBalance().toBigInteger();
        Double debitAmount = transferRequest.getAmount();
        if(availableBalance.intValue()<debitAmount.intValue()){
            return BankResponse.builder().responseMessage(AccountUtils.ACCOUNT_NOT_SUFFICIENT_BALANCE_MESSAGE).responseCode(AccountUtils.ACCOUNT_NOT_SUFFICIENT_BALANCE_CODE)  .accountInfo(AccountInfo.builder()
                    .accountName(userDebit.getFirstName() + " " + userDebit.getLastName())
                    .accountBalance(userDebit.getAccountBalance())
                    .accountNumber(transferRequest.getSourceAccount())
                    .build()).build();
        }
        userDebit.setAccountBalance(userDebit.getAccountBalance().subtract(BigDecimal.valueOf(transferRequest.getAmount())));
        userRepository.save(userDebit);
        User userCredit = userRepository.findByAccountNumber(transferRequest.getDestinationAccount());
        userCredit.setAccountBalance(userCredit.getAccountBalance().add(BigDecimal.valueOf(transferRequest.getAmount())));
        userRepository.save(userCredit);
        EmailDetails emailSource= EmailDetails.builder()
                .recipient(userDebit.getEmail())
                .subject("Transfer Successful")
                .messageBody("Your Transfer was successful for the amount $ " + transferRequest.getAmount() +"\n" +
                        "\n Your Account details: \n Account Name: "+userDebit.getFirstName()+ " " + userDebit.getLastName()+"\n Account Number: "+userDebit.getAccountNumber() + "\n Account Balance: $ "+ userDebit.getAccountBalance() )
                .build();
        emailService.sendEmailAlert(emailSource);
        EmailDetails emailDestinationAccount= EmailDetails.builder()
                .recipient(userCredit.getEmail())
                .subject("Amount Credited! ")
                .messageBody("There is an update in the account, You have credited "+ transferRequest.getAmount()  +
                        "\n Your Account details: \n Account Name: "+userCredit.getFirstName()+ " " + userCredit.getLastName()+"\n Account Number: "+userCredit.getAccountNumber()+ "\n Account Balance: $ "+ userCredit.getAccountBalance())
                .build();
        emailService.sendEmailAlert(emailDestinationAccount);
        //success message
        return BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountName(userDebit.getFirstName() + " " + userDebit.getLastName())
                        .accountBalance(userDebit.getAccountBalance())
                        .accountNumber(transferRequest.getSourceAccount())
                        .build())
                .build();

    }
    // balance Enquiry, name enquiry, credit,debit, transfer
}
