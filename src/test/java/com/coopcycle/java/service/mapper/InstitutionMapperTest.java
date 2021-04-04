package com.coopcycle.java.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstitutionMapperTest {

    private InstitutionMapper institutionMapper;

    @BeforeEach
    public void setUp() {
        institutionMapper = new InstitutionMapperImpl();
    }
}
