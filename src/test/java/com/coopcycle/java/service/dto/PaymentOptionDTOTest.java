package com.coopcycle.java.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.coopcycle.java.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PaymentOptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PaymentOptionDTO.class);
        PaymentOptionDTO paymentOptionDTO1 = new PaymentOptionDTO();
        paymentOptionDTO1.setId(1L);
        PaymentOptionDTO paymentOptionDTO2 = new PaymentOptionDTO();
        assertThat(paymentOptionDTO1).isNotEqualTo(paymentOptionDTO2);
        paymentOptionDTO2.setId(paymentOptionDTO1.getId());
        assertThat(paymentOptionDTO1).isEqualTo(paymentOptionDTO2);
        paymentOptionDTO2.setId(2L);
        assertThat(paymentOptionDTO1).isNotEqualTo(paymentOptionDTO2);
        paymentOptionDTO1.setId(null);
        assertThat(paymentOptionDTO1).isNotEqualTo(paymentOptionDTO2);
    }
}
