package org.extension.spring.data.repository.internal;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TypedQuerySpecificationQueryCreatorTest {

  @Mock
  private EntityManager entityManager;

  @BeforeAll
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSuccess() {
    final String jpql = "select max(1) from dual";
    new TypedQuerySpecificationQueryCreator().create(entityManager, () -> jpql, Person.class);
    verify(entityManager).createQuery(eq(jpql), eq(Person.class));
  }

  @Test
  public void testNullDomainAndSpecificationThrowsNPE() {
    Assertions.assertThatThrownBy(() ->
      new TypedQuerySpecificationQueryCreator().create(entityManager, null, null)
    ).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testNullEntityManagerThrowsNPE() {
    Assertions.assertThatThrownBy(() ->
      new TypedQuerySpecificationQueryCreator().create(null, () -> "SELECT 1", Person.class)
    ).isInstanceOf(NullPointerException.class);
  }

  @Entity
  static final class Person {

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
