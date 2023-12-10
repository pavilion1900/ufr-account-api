package ru.clevertec.entity;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Entity
public class Account {

    @Id
    private String id;
    private String signAppId;
    private String type;
    private String currency;
    private String status;
    private String paymentKindCode;
}
