package ru.clevertec.repository;

import java.util.List;

public interface Repository<T> {

    List<T> findByParams(String signAppId, String type, String currency, String status, String paymentKindCode);

    T save(T account);
}
