package ru.clevertec.service;

import java.util.List;

public interface Service<T> {

    List<T> findByParams(String signAppId, String type, String currency, String status, String paymentKindCode);

    T save(T accountDto);
}
