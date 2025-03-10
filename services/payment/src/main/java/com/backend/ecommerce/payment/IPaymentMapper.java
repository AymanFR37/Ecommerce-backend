package com.backend.ecommerce.payment;


import org.mapstruct.Mapper;

@Mapper
public interface IPaymentMapper {

    Payment toPayment(PaymentRequest request);

}
