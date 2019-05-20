package org.extension.spring.data.repository.internal;

import org.extension.spring.data.repository.specification.QuerySpecification;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.Collections;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class RepositorySpecificationExecutorImplTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Metamodel metamodel;
    @Mock
    private EntityType<Person> managedType;

    private RepositorySpecificationExecutorImpl repositorySpecificationExecutor;

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


    @Test
    public void testFindAllWithTypedQuerySpecification() {

        final String jpql = "FROM Person";

        final TypedQuery typedQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(eq(jpql), eq(Person.class)))
                .thenReturn(typedQuery);

        when(typedQuery.getResultList())
                .thenReturn(Collections.emptyList());

        repositorySpecificationExecutor.findAll(
            (TypedQuerySpecification<Person>) () -> jpql
        );

        verify(entityManager).createQuery(eq(jpql), eq(Person.class));
        verify(typedQuery).getResultList();
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

