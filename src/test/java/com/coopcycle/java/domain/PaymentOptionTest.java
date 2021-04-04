package com.coopcycle.java.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.coopcycle.java.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentOptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentOption.class);
        PaymentOption paymentOption1 = new PaymentOption();
        paymentOption1.setId(1L);
        PaymentOption paymentOption2 = new PaymentOption();
        paymentOption2.setId(paymentOption1.getId());
        assertThat(paymentOption1).isEqualTo(paymentOption2);
        paymentOption2.setId(2L);
        assertThat(paymentOption1).isNotEqualTo(paymentOption2);
        paymentOption1.setId(null);
        assertThat(paymentOption1).isNotEqualTo(paymentOption2);
    }
}
