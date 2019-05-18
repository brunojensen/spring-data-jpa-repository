package org.extension.spring.data.repository.internal;

import org.extension.spring.data.repository.specification.TypedQuerySpecification;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Objects;

class TypedQuerySpecificationProcessor {

    private TypedQuerySpecificationProcessor() {}

    static <T> TypedQuery<T> process(EntityManager entityManager, Class<T> domainClass, TypedQuerySpecification<T> specification) {
        Objects.requireNonNull(entityManager);
        Objects.requireNonNull(domainClass);
        Objects.requireNonNull(specification);

        final TypedQuery<T> query = entityManager.createQuery(specification.query(), domainClass);
        specification.withPredicate(query);
        return query;
    }
}
