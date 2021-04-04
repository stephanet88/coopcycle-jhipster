package com.coopcycle.java.cucumber;

import com.coopcycle.java.CoopcycleApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = CoopcycleApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
