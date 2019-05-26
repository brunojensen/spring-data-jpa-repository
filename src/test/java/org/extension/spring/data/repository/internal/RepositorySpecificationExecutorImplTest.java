package org.extension.spring.data.repository.internal;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping;
import org.extension.spring.data.repository.specification.QuerySpecification;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class RepositorySpecificationExecutorImplTest {

  @Mock
  private EntityManager entityManager;
  @Mock
  private Metamodel metamodel;
  @Mock
  private EntityType<Person> managedType;

  private RepositorySpecificationExecutorImpl<Person, String> repositorySpecificationExecutor;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    when(entityManager.getMetamodel()).thenReturn(metamodel);
    when(entityManager.getDelegate()).thenReturn(entityManager);
    when(metamodel.managedType(Person.class)).thenReturn(managedType);
    repositorySpecificationExecutor = new RepositorySpecificationExecutorImpl<>(
        Person.class, entityManager
    );
  }

  /* START TEST FIND METHOD */
  @Test(expected = IllegalArgumentException.class)
  public void testFindQuerySpecification_UnsupportedThrowsIllegalArgException() {
    repositorySpecificationExecutor.find((QuerySpecification) () -> "SELECT * FROM Person");
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

    assertThat(person, nullValue());

    verify(entityManager, never()).createQuery(anyString());
  }

  @Test
  public void testFindTypedQuerySpecification_Success() {

    final String query = "SELECT p FROM Person";

    final TypedQuery typedQuery = mock(TypedQuery.class);

    when(entityManager.createQuery(eq(query), eq(Person.class)))
        .thenReturn(typedQuery);

    when(typedQuery.getSingleResult())
        .thenReturn(new Person());

    final Person person = repositorySpecificationExecutor.find(
        (TypedQuerySpecification) () -> query, Person.class);

    assertThat(person, not(nullValue()));

    verify(entityManager).createQuery(eq(query), eq(Person.class));
    verify(typedQuery).getSingleResult();
  }

  @Test
  public void testFindTypedNativeQuerySpecification_Success() {

    final String query = "SELECT * FROM Person";

    final Query nativeQuery = mock(TypedQuery.class);

    when(entityManager.createNativeQuery(eq(query), eq(Person.class)))
        .thenReturn(nativeQuery);

    when(nativeQuery.getSingleResult())
        .thenReturn(new Person());

    final Person person = repositorySpecificationExecutor.find(
        (TypedNativeQuerySpecification) () -> query, Person.class);

    assertThat(person, not(nullValue()));

    verify(entityManager).createNativeQuery(eq(query), eq(Person.class));
    verify(nativeQuery).getSingleResult();
  }

  /* END TEST FIND METHOD */

  /* START TEST FIND ALL METHOD */

  @Test(expected = IllegalArgumentException.class)
  public void testFindAllQuerySpecification_UnsupportedThrowsIllegalArgException() {
    repositorySpecificationExecutor.findAll((QuerySpecification) () -> "SELECT * FROM Person");
  }

  @Test
  public void testFindAllWithTypedQuerySpecification_paging() {

    final String jpql = "SELECT p FROM Person p";

    final String jpqlSorted = "SELECT p FROM Person p order by p.email asc";

    final TypedQuery typedQuery = mock(TypedQuery.class);

    when(entityManager.createQuery(eq(jpqlSorted), eq(Person.class)))
        .thenReturn(typedQuery);

    final String jpqlCount = "select count(p) FROM Person p";

    final TypedQuery queryCount = mock(TypedQuery.class);

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

    assertThat(list, IsNull.notNullValue());
    assertThat(list.getTotalElements(), equalTo(1L));

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

    assertThat(persons, not(nullValue()));
    assertThat(persons.size(), equalTo(0));

    verify(entityManager, never()).createQuery(anyString());
  }

  @Test
  public void testCountWithTypedQuerySpecification() {

    final String jpql = "select count(1) from Person p where p.name like :name";

    final Query query = mock(Query.class);

    when(entityManager.createQuery(eq(jpql)))
        .thenReturn(query);

    when(query.getSingleResult())
        .thenReturn(1L);

    repositorySpecificationExecutor.count(
        (QuerySpecification) () -> "select * from Person p where p.name like :name"
    );

    verify(entityManager).createQuery(eq(jpql));
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

    assertThat(count, equalTo(0L));

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

