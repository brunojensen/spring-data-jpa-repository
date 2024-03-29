package org.extension.spring.data.repository.internal;

import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;

final class TypedNativeQuerySpecificationQueryCreator implements
  SpecificationQueryCreator<TypedNativeQuerySpecification, Query> {

  @Override
  public Query create(EntityManager entityManager, TypedNativeQuerySpecification specification,
                      Class<?> domainClass) {
    Objects.requireNonNull(entityManager);
    Objects.requireNonNull(specification);

    final Query query = createQuery(entityManager, domainClass, specification);
    specification.withPredicate(query);
    return query;
  }

  private Query createQuery(EntityManager entityManager, Class<?> domainClass,
                            TypedNativeQuerySpecification specification) {
    if (null == domainClass) {
      return entityManager.createNativeQuery(specification.query());
    }
    if (domainClass.isAnnotationPresent(TypedAsSqlResultSetMapping.class)) {
      return entityManager.createNativeQuery(specification.query(),
        domainClass.getAnnotation(TypedAsSqlResultSetMapping.class).value());
    }
    return entityManager.createNativeQuery(specification.query(), domainClass);
  }

}
