package com.backend.ecommerce.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Getter
public class CustomerNotFoundException extends RuntimeException {
    private final String msg;
}
