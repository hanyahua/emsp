package com.volvo.emsp;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration-test")
@DataJpaTest
public abstract class BaseDataJpaIntegrationTest {

}
