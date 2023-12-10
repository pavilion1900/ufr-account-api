package ru.clevertec.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.clevertec.dto.AccountDto;
import ru.clevertec.entity.Account;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDto toAccountDto(Account account);

    @Mapping(target = "status", constant = "SR")
    @Mapping(target = "paymentKindCode", constant = "19")
    Account toAccount(AccountDto accountDto);
}
