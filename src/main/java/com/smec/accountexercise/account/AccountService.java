package com.smec.accountexercise.account;

import com.smec.accountexercise.http.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository delegate;

    public AccountDto save(AccountDto dto) {
        var saved = delegate.save(Account.builder().id(dto.getId()).name(dto.getName()).build());
        return AccountDto.builder().name(saved.getName()).id(saved.getId()).build();
    }

    public AccountDto findById(String id) {
        return delegate.findById(id)
                .map(it -> AccountDto.builder().name(it.getName()).id(it.getId()).build())
                .orElseThrow(NotFoundException::new);
    }

    public void delete(String id) {
        delegate.deleteById(id);
    }
}
