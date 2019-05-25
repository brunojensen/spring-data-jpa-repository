package org.extension.spring.data.repository.internal;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TypedNativeQuerySpecificationProcessorTest {

  @Mock
  private EntityManager entityManager;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testProcessorSuccess() {
    final String jpql = "select max(1) from dual";
    TypedNativeQuerySpecificationProcessor.process(entityManager, Person.class, () -> jpql);
    verify(entityManager).createNativeQuery(eq(jpql), eq(Person.class));
  }

  @Test
  public void testProcessorSuccessWithResultSetMapping() {
    final String jpql = "select max(1) from dual";
    TypedNativeQuerySpecificationProcessor.process(entityManager, PersonResultMapping.class, () -> jpql);
    verify(entityManager).createNativeQuery(eq(jpql), eq("PersonResultMapping"));
  }

  @Test(expected = NullPointerException.class)
  public void testProcessorNullDomainAndSpecificationThrowsNPE() {
    TypedNativeQuerySpecificationProcessor.process(entityManager, null, null);
  }

  @Test(expected = NullPointerException.class)
  public void testProcessorNullEntityManagerThrowsNPE() {
    TypedNativeQuerySpecificationProcessor.process(null, Person.class, () -> "SELECT 1");
  }

  @TypedAsSqlResultSetMapping("PersonResultMapping")
  static class PersonResultMapping {

    private String name;

    public PersonResultMapping(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  @SqlResultSetMapping(
      name = "PersonResultMapping",
      entities = @EntityResult(
          entityClass = PersonResultMapping.class,
          fields = {
              @FieldResult(name = "name", column = "name")
          }
      )
  )
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
