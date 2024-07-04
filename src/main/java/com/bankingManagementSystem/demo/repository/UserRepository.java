package com.bankingManagementSystem.demo.repository;

import com.bankingManagementSystem.demo.model.BankResponse;
import com.bankingManagementSystem.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
        Boolean existsByEmail(String email);
        Boolean existsByAccountNumber(String accountNumber);
        User findByAccountNumber(String accountNumber);
}
