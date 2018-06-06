package org.extension.spring.data.repository.internal;

import org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Objects;

class TypedNativeQuerySpecificationProcessor {

    private TypedNativeQuerySpecificationProcessor() {}

    static <T> Query process(EntityManager entityManager, Class<T> domainClass, TypedNativeQuerySpecification<T> specification) {
        Objects.requireNonNull(entityManager);
        Objects.requireNonNull(domainClass);
        Objects.requireNonNull(specification);

        final Query query = createQuery(entityManager, domainClass, specification);
        specification.toPredicate(query);
        return query;
    }

    private static <T> Query createQuery(EntityManager entityManager, Class<T> domainClass, TypedNativeQuerySpecification<T> specification) {
        if(domainClass.isAnnotationPresent(TypedAsSqlResultSetMapping.class)) {
            return entityManager.createNativeQuery(specification.query(), domainClass.getAnnotation(TypedAsSqlResultSetMapping.class).value());
        }
        return entityManager.createNativeQuery(specification.query(), domainClass);
    }
}
