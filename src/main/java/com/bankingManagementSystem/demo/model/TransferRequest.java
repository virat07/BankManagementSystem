package com.bankingManagementSystem.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest {

    private String sourceAccount;
    private String destinationAccount;
    private Double Amount;

}
