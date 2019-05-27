package org.extension.spring.data.repository.internal;

import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;

class TypedQuerySpecificationProcessor implements SpecificationProcessor<TypedQuerySpecification, TypedQuery<?>> {

  @Override
  public TypedQuery<?> process(EntityManager entityManager, TypedQuerySpecification specification,
      Class<?> domainClass) {
    Objects.requireNonNull(entityManager);
    Objects.requireNonNull(domainClass);
    Objects.requireNonNull(specification);

    final TypedQuery<?> query = entityManager.createQuery(specification.query(), domainClass);
    specification.withPredicate(query);
    return query;
  }
}
