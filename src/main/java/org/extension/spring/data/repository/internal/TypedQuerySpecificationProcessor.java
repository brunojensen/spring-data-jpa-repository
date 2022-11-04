package org.extension.spring.data.repository.internal;

import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.extension.spring.data.repository.specification.TypedQuerySpecification;

final class TypedQuerySpecificationProcessor implements SpecificationProcessor<TypedQuerySpecification, Query> {

  @Override
  public Query process(EntityManager entityManager, TypedQuerySpecification specification,
                       Class<?> domainClass) {
    Objects.requireNonNull(entityManager);
    Objects.requireNonNull(specification);
    final Query query = createQuery(entityManager, specification, domainClass);
    specification.withPredicate(query);
    return query;
  }

  private Query createQuery(EntityManager entityManager, TypedQuerySpecification specification, Class<?> domainClass) {
    if (null == domainClass) {
      return entityManager.createQuery(specification.query());
    }
    return entityManager.createQuery(specification.query(), domainClass);
  }
}
