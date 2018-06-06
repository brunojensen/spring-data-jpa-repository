package com.bea.spring.repository.internal.specification;

import com.bea.spring.repository.TypedQuerySpecification;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Objects;

class TypedQuerySpecificationProcessor {

    public static <T> TypedQuery<T> process(EntityManager entityManager, Class<T> domainClass, TypedQuerySpecification<T> specification) {
        Objects.requireNonNull(entityManager);
        Objects.requireNonNull(domainClass);
        Objects.requireNonNull(specification);

        final TypedQuery<T> query = entityManager.createQuery(specification.query(), domainClass);
        specification.toPredicate(query);
        return query;
    }
}
