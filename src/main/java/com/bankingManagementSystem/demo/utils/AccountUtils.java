package com.bankingManagementSystem.demo.utils;

import java.time.Year;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE= "001";
    public static final String ACCOUNT_EXIST_MESSAGE="Already account exists";
    public static final String ACCOUNT_NOT_EXISTS_CODE= "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE="Account does not exist";
    public static final String DESTINATION_ACCOUNT_NOT_EXISTS_CODE= "009";
    public static final String DESTINATION_ACCOUNT_NOT_EXIST_MESSAGE="Destination Account does not exist";
    public static final String ACCOUNT_FOUND_CODE= "201";
    public static final String ACCOUNT_FOUND_MESSAGE="Account found";
    public static final String ACCOUNT_CREATION_SUCCESS= "200";
    public static final String ACCOUNT_CREATION_MESSAGE="Account Created Successfully!";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account Balance Updated!";
    public static final String ACCOUNT_NOT_SUFFICIENT_BALANCE_CODE = "006";
    public static final String ACCOUNT_NOT_SUFFICIENT_BALANCE_MESSAGE = "Not Sufficient Balance!";
    public static String generateAccountNumber(){
        /*
         * 2023 + random 6 digits
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        // generate a random number b/w min and max

        int randomNumber = (int)Math.floor(Math.random() * (max-min + 1) + min);

        // convert the current year and randomnumber to strings and concatnate.
        String year = String.valueOf(currentYear);
        String randNumber = String.valueOf(randomNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randNumber).toString();

    }
}
