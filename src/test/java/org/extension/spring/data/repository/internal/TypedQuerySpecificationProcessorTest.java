package org.extension.spring.data.repository.internal;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TypedQuerySpecificationProcessorTest {

  @Mock
  private EntityManager entityManager;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testProcessorSuccess() {
    final String jpql = "select max(1) from dual";
    new TypedQuerySpecificationProcessor().process(entityManager, () -> jpql, Person.class);
    verify(entityManager).createQuery(eq(jpql), eq(Person.class));
  }

  @Test(expected = NullPointerException.class)
  public void testProcessorNullDomainAndSpecificationThrowsNPE() {
    new TypedQuerySpecificationProcessor().process(entityManager, null, null);
  }

  @Test(expected = NullPointerException.class)
  public void testProcessorNullEntityManagerThrowsNPE() {
    new TypedQuerySpecificationProcessor().process(null, () -> "SELECT 1", Person.class);
  }

  @Entity
  public final class Person {

    @Id
    private String id;

    private String name;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

}
