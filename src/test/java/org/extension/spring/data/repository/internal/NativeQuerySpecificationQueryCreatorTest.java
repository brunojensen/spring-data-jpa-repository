package org.extension.spring.data.repository.internal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.EntityManager;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NativeQuerySpecificationQueryCreatorTest {

  @Mock
  private EntityManager entityManager;

  @BeforeAll
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSuccess() {
    final String jpql = "select max(1) from dual";
    new NativeQuerySpecificationQueryCreator().create(entityManager, () -> jpql, null);
    verify(entityManager).createNativeQuery(eq(jpql));
  }

  @Test
  public void testNullSpecificationThrowsNPE() {
    Assertions.assertThatThrownBy(() ->
      new NativeQuerySpecificationQueryCreator().create(entityManager, null, null)
    ).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testNullEntityManagerThrowsNPE() {
    Assertions.assertThatThrownBy(() ->
      new NativeQuerySpecificationQueryCreator().create(null, () -> "SELECT 1", null)
    ).isInstanceOf(NullPointerException.class);
  }
}
