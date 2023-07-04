package com.microservice.accountservice.repository;

import com.microservice.accountservice.data.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository  extends ReactiveCrudRepository<Account,String> {
}
