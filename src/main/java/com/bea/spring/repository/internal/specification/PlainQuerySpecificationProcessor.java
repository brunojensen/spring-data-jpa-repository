package com.bea.spring.repository.internal.specification;

import com.bea.spring.repository.PlainQuerySpecification;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Objects;

class PlainQuerySpecificationProcessor {

    public static Query process(EntityManager entityManager, PlainQuerySpecification specification) {
        Objects.requireNonNull(entityManager);
        Objects.requireNonNull(specification);

        final Query query = entityManager.createQuery(specification.query());
        specification.toPredicate(query);
        return query;
    }
}
