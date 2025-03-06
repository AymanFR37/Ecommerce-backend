package com.backend.ecommerce.customer;

import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class Address {
    private String street;
    private String houseNumber;
    private String zipCode;
}
