package com.coopcycle.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentOptionMapperTest {

    private PaymentOptionMapper paymentOptionMapper;

    @BeforeEach
    public void setUp() {
        paymentOptionMapper = new PaymentOptionMapperImpl();
    }
}
