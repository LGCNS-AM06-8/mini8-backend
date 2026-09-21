package com.mini8.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/** 스프링이 뜨는지만 확인한다. MariaDB 없이 H2로 돈다(application-test.yml). 기능 테스트는 각 도메인 담당이 추가한다. */
@SpringBootTest
@ActiveProfiles("test")
class BackendApplicationTests {

  @Test
  void contextLoads() {}
}
