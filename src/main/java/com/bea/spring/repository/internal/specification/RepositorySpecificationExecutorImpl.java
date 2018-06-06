package com.bea.spring.repository.internal.specification;

import com.bea.spring.repository.DefaultSpecification;
import com.bea.spring.repository.PlainQuerySpecification;
import com.bea.spring.repository.RepositorySpecificationExecutor;
import com.bea.spring.repository.TypedQuerySpecification;
import com.bea.spring.repository.internal.annotations.QueryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class RepositorySpecificationExecutorImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements RepositorySpecificationExecutor<T, ID> {

    private final EntityManager entityManager;

    public RepositorySpecificationExecutorImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    public T findBy(DefaultSpecification<T> specification) {
        switch (specification.getClass().getAnnotation(QueryType.class).value()) {
            case PLAIN:
                throw new IllegalArgumentException("PlainQuerySpecification are not supported.");
            case TYPED:
                return (T) TypedQuerySpecificationProcessor
                        .process(entityManager, getDomainClass(), (TypedQuerySpecification<T>) specification)
                        .getSingleResult();
            case CRITERIA:
            default:
                return findOne((Specification<T>) specification);
        }
    }

    public List<T> searchBy(DefaultSpecification<T> specification) {
        switch (specification.getClass().getAnnotation(QueryType.class).value()) {
            case PLAIN:
                throw new IllegalArgumentException("PlainQuerySpecification are not supported.");
            case TYPED:
                return TypedQuerySpecificationProcessor
                        .process(entityManager, getDomainClass(), (TypedQuerySpecification<T>) specification)
                        .getResultList();
            case CRITERIA:
            default:
                return findAll((Specification<T>) specification);
        }
    }

    public long count(DefaultSpecification<T> specification) {
        switch (specification.getClass().getAnnotation(QueryType.class).value()) {
            case PLAIN:
                return ((Number) PlainQuerySpecificationProcessor
                        .process(entityManager, (PlainQuerySpecification) specification)
                        .getSingleResult())
                        .longValue();
            case TYPED:
                throw new IllegalArgumentException("TypedQuerySpecification are not supported.");
            case CRITERIA:
            default:
                return count((Specification<T>) specification);
        }
    }
}