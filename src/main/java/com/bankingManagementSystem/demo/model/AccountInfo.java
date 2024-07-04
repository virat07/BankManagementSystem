package com.bankingManagementSystem.demo.model;

import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "accountInfo")
public class AccountInfo {
    private String accountName;
    private BigDecimal accountBalance;
    private String accountNumber;
}
