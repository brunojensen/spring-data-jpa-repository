package org.extension.spring.data.repository.internal;

import org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping;
import org.extension.spring.data.repository.specification.QuerySpecification;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class RepositorySpecificationExecutorImplTest {

  @Mock
  private EntityManager entityManager;
  @Mock
  private Metamodel metamodel;
  @Mock
  private EntityType<Person> managedType;

  private RepositorySpecificationExecutorImpl<Person, String> repositorySpecificationExecutor;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    when(entityManager.getMetamodel()).thenReturn(metamodel);
    when(entityManager.getDelegate()).thenReturn(entityManager);
    when(metamodel.managedType(Person.class)).thenReturn(managedType);
    repositorySpecificationExecutor = new RepositorySpecificationExecutorImpl<>(
      Person.class, entityManager
    );
  }

  /* START TEST FIND METHOD */
  @Test
  public void testFindQuerySpecification() {
    final Query mockedQuery = mock(Query.class);

    final String query = "SELECT email FROM Person";

    when(entityManager.createQuery(eq(query)))
      .thenReturn(mockedQuery);

    when(mockedQuery.getSingleResult())
      .thenReturn("");

    repositorySpecificationExecutor.find((QuerySpecification) () -> query, String.class);
  }

  @Test
  public void testFindQuerySpecification_notSatisfied() {

    final Person person = repositorySpecificationExecutor.find(new TypedQuerySpecification() {
      @Override
      public String query() {
        return "SELECT p FROM Person";
      }

      @Override
      public boolean isSatisfied() {
        return false;
      }
    }, Person.class);

    assertThat(person).isNull();

    verify(entityManager, never()).createQuery(anyString());
  }

  @Test
  public void testFindTypedQuerySpecification_Success() {

    final String query = "SELECT p FROM Person";

    final TypedQuery<Person> typedQuery = (TypedQuery<Person>) mock(TypedQuery.class);

    when(entityManager.createQuery(eq(query), eq(Person.class)))
      .thenReturn(typedQuery);

    when(typedQuery.getSingleResult())
      .thenReturn(new Person());

    final Person person = repositorySpecificationExecutor.find(
      (TypedQuerySpecification) () -> query, Person.class);

    assertThat(person).isNotNull();

    verify(entityManager).createQuery(eq(query), eq(Person.class));
    verify(typedQuery).getSingleResult();
  }

  @Test
  public void testFindTypedNativeQuerySpecification_Success() {

    final String query = "SELECT * FROM Person";

    final Query nativeQuery = mock(Query.class);

    when(entityManager.createNativeQuery(eq(query), eq(Person.class)))
      .thenReturn(nativeQuery);

    when(nativeQuery.getSingleResult())
      .thenReturn(new Person());

    final Person person = repositorySpecificationExecutor.find(
      (TypedNativeQuerySpecification) () -> query, Person.class);

    assertThat(person).isNotNull();

    verify(entityManager).createNativeQuery(eq(query), eq(Person.class));
    verify(nativeQuery).getSingleResult();
  }

  /* END TEST FIND METHOD */

  /* START TEST FIND ALL METHOD */

  @Test
  public void testFindAllQuerySpecification() {
    final Query mockedQuery = mock(Query.class);

    final String query = "SELECT email FROM Person";

    when(entityManager.createQuery(eq(query)))
      .thenReturn(mockedQuery);

    when(mockedQuery.getSingleResult())
      .thenReturn("");

    repositorySpecificationExecutor.findAll((QuerySpecification) () -> query, String.class);
  }

  @Test
  public void testFindAllWithTypedQuerySpecification_paging() {

    final String jpql = "SELECT p FROM Person p";

    final String jpqlSorted = "SELECT p FROM Person p order by p.email asc";

    final TypedQuery typedQuery = mock(TypedQuery.class);

    when(entityManager.createQuery(eq(jpqlSorted), eq(Person.class)))
      .thenReturn(typedQuery);

    final String jpqlCount = "select count(p) FROM Person p";

    final Query queryCount = mock(Query.class);

    when(entityManager.createQuery(eq(jpqlCount)))
      .thenReturn(queryCount);

    when(queryCount.getSingleResult())
      .thenReturn(1);

    final PageRequest pageable = PageRequest.of(0, 20, Sort.by("email"));

    when(typedQuery.setFirstResult(anyInt()))
      .thenReturn(typedQuery);

    when(typedQuery.setMaxResults(anyInt()))
      .thenReturn(typedQuery);

    when(typedQuery.getResultList())
      .thenReturn(Arrays.asList(new Person()));

    final Page<Person> list = repositorySpecificationExecutor.findAll(
      (TypedQuerySpecification) () -> jpql, pageable
    );

    assertThat(list).isNotNull();
    assertThat(list).hasSize(1);

    verify(entityManager).createQuery(eq(jpqlSorted), eq(Person.class));
    verify(typedQuery).setFirstResult((int) pageable.getOffset());
    verify(typedQuery).setMaxResults(pageable.getPageSize());
    verify(typedQuery).getResultList();
  }


  @Test
  public void testFindAllWithTypedQuerySpecification_withLambda() {

    final String jpql = "FROM Person";

    final TypedQuery typedQuery = mock(TypedQuery.class);

    when(entityManager.createQuery(eq(jpql), eq(Person.class)))
      .thenReturn(typedQuery);

    when(typedQuery.getResultList())
      .thenReturn(Collections.emptyList());

    repositorySpecificationExecutor.findAll(
      (TypedQuerySpecification) () -> jpql
    );

    verify(entityManager).createQuery(eq(jpql), eq(Person.class));
    verify(typedQuery).getResultList();
  }

  @Test
  public void testFindAllWithTypedQuerySpecification_withAnonymousClass() {

    final String jpql = "FROM Person";

    final TypedQuery typedQuery = mock(TypedQuery.class);

    when(entityManager.createQuery(eq(jpql), eq(Person.class)))
      .thenReturn(typedQuery);

    when(typedQuery.getResultList())
      .thenReturn(Collections.emptyList());

    repositorySpecificationExecutor.findAll(new TypedQuerySpecification() {
      @Override
      public String query() {
        return jpql;
      }
    });

    verify(entityManager).createQuery(eq(jpql), eq(Person.class));
    verify(typedQuery).getResultList();
  }

  @Test
  public void testFindAllWithTypedQuerySpecification_withConcreteClass() {

    final String jpql = "FROM Person";

    final TypedQuery typedQuery = mock(TypedQuery.class);

    when(entityManager.createQuery(eq(jpql), eq(Person.class)))
      .thenReturn(typedQuery);

    when(typedQuery.getResultList())
      .thenReturn(Collections.emptyList());

    repositorySpecificationExecutor.findAll(new PersonSpecification(jpql));

    verify(entityManager).createQuery(eq(jpql), eq(Person.class));
    verify(typedQuery).getResultList();
  }

  @Test
  public void testFindAllWithTypedQuerySpecification_withConcreteClassIn2ndLevel() {

    final String jpql = "FROM Person";

    final TypedQuery typedQuery = mock(TypedQuery.class);

    when(entityManager.createQuery(eq(jpql), eq(Person.class)))
      .thenReturn(typedQuery);

    when(typedQuery.getResultList())
      .thenReturn(Collections.emptyList());

    repositorySpecificationExecutor.findAll(new PersonSpecification2(jpql));

    verify(entityManager).createQuery(eq(jpql), eq(Person.class));
    verify(typedQuery).getResultList();
  }

  @Test
  public void testFindAllWithTypedNativeQuerySpecification_withJPAEntity() {

    final String jpql = "SELECT * FROM Person";

    final Query query = mock(Query.class);

    when(entityManager.createNativeQuery(eq(jpql), eq(Person.class)))
      .thenReturn(query);

    when(query.getResultList())
      .thenReturn(Collections.emptyList());

    repositorySpecificationExecutor.findAll(
      (TypedNativeQuerySpecification) () -> jpql
    );

    verify(entityManager).createNativeQuery(eq(jpql), eq(Person.class));
    verify(query).getResultList();
  }

  @Test
  public void testFindAllWithTypedNativeQuerySpecification_withResultMappingPOJO() {

    final String jpql = "SELECT * FROM Person";

    final Query query = mock(Query.class);

    when(entityManager.createNativeQuery(eq(jpql), eq("PersonResultMapping")))
      .thenReturn(query);

    when(query.getResultList())
      .thenReturn(Collections.emptyList());

    repositorySpecificationExecutor.findAll(
      (TypedNativeQuerySpecification) () -> jpql, PersonResultMapping.class
    );

    verify(entityManager).createNativeQuery(eq(jpql), eq("PersonResultMapping"));
    verify(query).getResultList();
  }

  @Test
  public void testFindAllQuerySpecification_notSatisfied() {

    final List<Person> persons = repositorySpecificationExecutor
      .findAll(new TypedNativeQuerySpecification() {
        @Override
        public String query() {
          return "SELECT p FROM Person";
        }

        @Override
        public boolean isSatisfied() {
          return false;
        }
      });

    assertThat(persons).isNotNull();
    assertThat(persons).hasSize(0);

    verify(entityManager, never()).createQuery(anyString());
  }

  @Test
  public void testCountWithTypedQuerySpecification() {

    final String jpql = "select count(p) from Person p where p.name like :name";

    final Query query = mock(Query.class);

    when(entityManager.createQuery(eq(jpql)))
      .thenReturn(query);

    when(query.getSingleResult())
      .thenReturn(1L);

    repositorySpecificationExecutor.count(
      (TypedQuerySpecification) () -> "select p from Person p where p.name like :name"
    );

    verify(entityManager).createQuery(eq(jpql));
    verify(query).getSingleResult();
  }

  @Test
  public void testCountWithTypedNativeQuerySpecification() {

    final String jpql = "select count(1) from Person p where p.name like :name";

    final Query query = mock(Query.class);

    when(entityManager.createNativeQuery(eq(jpql)))
      .thenReturn(query);

    when(query.getSingleResult())
      .thenReturn(1L);

    repositorySpecificationExecutor.count(
      (TypedNativeQuerySpecification) () -> "select * from Person p where p.name like :name"
    );

    verify(entityManager).createNativeQuery(eq(jpql));
    verify(query).getSingleResult();
  }

  @Test
  public void testCountQuerySpecification_notSatisfied() {

    long count = repositorySpecificationExecutor.count(new QuerySpecification() {
      @Override
      public String query() {
        return "SELECT count(*) FROM Person";
      }

      @Override
      public boolean isSatisfied() {
        return false;
      }
    });

    assertThat(count).isZero();

    verify(entityManager, never()).createQuery(anyString());
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

  private class PersonSpecification implements TypedQuerySpecification {

    private String jpql;

    public PersonSpecification(String jpql) {
      this.jpql = jpql;
    }


    @Override
    public String query() {
      return jpql;
    }
  }

  private class PersonSpecification2 extends PersonSpecification {

    public PersonSpecification2(String jpql) {
      super(jpql);
    }
  }
}

