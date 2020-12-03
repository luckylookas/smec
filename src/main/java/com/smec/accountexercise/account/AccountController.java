package com.smec.accountexercise.account;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public AccountDto get(@PathVariable("id") String id) {
        return accountService.findById(id);
    }

    @PostMapping("/")
    public AccountDto post(@RequestBody AccountDto account) {
        return accountService.save(account.toBuilder().id(null).build());
    }

    @PutMapping("/{id}")
    public AccountDto put(@PathVariable("id") String id, @RequestBody AccountDto account) {
        return accountService.save(account.toBuilder().id(id).build());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        accountService.delete(id);
    }
}
