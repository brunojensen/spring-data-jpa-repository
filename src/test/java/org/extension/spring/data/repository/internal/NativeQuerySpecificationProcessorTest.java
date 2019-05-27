package org.extension.spring.data.repository.internal;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import javax.persistence.EntityManager;
import org.extension.spring.data.repository.internal.RepositorySpecificationExecutorImplTest.Person;
import org.extension.spring.data.repository.specification.QuerySpecification;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NativeQuerySpecificationProcessorTest {

  @Mock
  private EntityManager entityManager;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testProcessorSuccess() {
    final String jpql = "select max(1) from dual";
    new NativeQuerySpecificationProcessor().process(entityManager, () -> jpql, null);
    verify(entityManager).createNativeQuery(eq(jpql));
  }

  @Test(expected = NullPointerException.class)
  public void testProcessorNullSpecificationThrowsNPE() {
    new NativeQuerySpecificationProcessor().process(entityManager, null, null);
  }

  @Test(expected = NullPointerException.class)
  public void testProcessorNullEntityManagerThrowsNPE() {
    new NativeQuerySpecificationProcessor().process(null, () -> "SELECT 1", null);
  }
}
