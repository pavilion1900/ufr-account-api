package ru.clevertec.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.clevertec.dto.AccountDto;
import ru.clevertec.entity.Account;
import ru.clevertec.mapper.AccountMapper;
import ru.clevertec.repository.AccountRepository;
import ru.clevertec.repository.Repository;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountService implements Service<AccountDto> {

    private static final Service<AccountDto> INSTANCE = new AccountService();
    private final Repository<Account> accountRepository = AccountRepository.getInstance();

    public static Service<AccountDto> getInstance() {
        return INSTANCE;
    }

    @Override
    public List<AccountDto> findByParams(String signAppId, String type, String currency, String status, String paymentKindCode) {
        return accountRepository.findByParams(signAppId, type, currency, status, paymentKindCode).stream()
                .map(AccountMapper.INSTANCE::toAccountDto)
                .toList();
    }

    @Override
    public AccountDto save(AccountDto accountDto) {
        Account savedAccount = accountRepository.save(AccountMapper.INSTANCE.toAccount(accountDto));
        return AccountMapper.INSTANCE.toAccountDto(savedAccount);
    }
}
