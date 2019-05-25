package org.extension.spring.data.repository.internal;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class QuerySpecificationProcessorTest {

  @Mock
  private EntityManager entityManager;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testProcessorSuccess() {
    final String jpql = "select max(1) from dual";
    QuerySpecificationProcessor.process(entityManager, () -> jpql);
    verify(entityManager).createQuery(eq(jpql));
  }

  @Test(expected = NullPointerException.class)
  public void testProcessorNullSpecificationThrowsNPE() {
    QuerySpecificationProcessor.process(entityManager, null);
  }

  @Test(expected = NullPointerException.class)
  public void testProcessorNullEntityManagerThrowsNPE() {
    QuerySpecificationProcessor.process(null, () -> "SELECT 1");
  }
}
