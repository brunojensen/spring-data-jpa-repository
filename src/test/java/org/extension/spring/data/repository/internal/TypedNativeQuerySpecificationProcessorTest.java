package org.extension.spring.data.repository.internal;

import org.assertj.core.api.Assertions;
import org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TypedNativeQuerySpecificationProcessorTest {

  @Mock
  private EntityManager entityManager;

  @BeforeAll
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testProcessorSuccess() {
    final String jpql = "select max(1) from dual";
    new TypedNativeQuerySpecificationProcessor().process(entityManager, () -> jpql, Person.class);
    verify(entityManager).createNativeQuery(eq(jpql), eq(Person.class));
  }

  @Test
  public void testProcessorSuccessWithResultSetMapping() {
    final String jpql = "select max(1) from dual";
    new TypedNativeQuerySpecificationProcessor().process(entityManager, () -> jpql, PersonResultMapping.class);
    verify(entityManager).createNativeQuery(eq(jpql), eq("PersonResultMapping"));
  }

  @Test
  public void testProcessorNullDomainAndSpecificationThrowsNPE() {
    Assertions.assertThatThrownBy(() ->
      new TypedNativeQuerySpecificationProcessor().process(entityManager, null, null)
    ).isInstanceOf(NullPointerException.class);
  }

  @Test
  public void testProcessorNullEntityManagerThrowsNPE() {
    Assertions.assertThatThrownBy(() ->
      new TypedNativeQuerySpecificationProcessor().process(null, () -> "SELECT 1", Person.class)
    ).isInstanceOf(NullPointerException.class);
  }

  @TypedAsSqlResultSetMapping("PersonResultMapping")
  static class PersonResultMapping {

    private final String name;

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
