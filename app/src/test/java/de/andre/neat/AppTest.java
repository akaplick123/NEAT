package de.andre.neat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AppTest {

  @Test
  void appHasAGreeting() {
    App classUnderTest = new App();
    assertThat(classUnderTest.getGreeting()).isEqualTo("app should have a greeting");
  }
}
