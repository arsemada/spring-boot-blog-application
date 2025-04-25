package com.example.spring_boot_blog_application.repositories;

import com.example.spring_boot_blog_application.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findOneByEmail(String email);


}
