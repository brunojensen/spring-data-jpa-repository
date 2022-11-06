package org.extension.spring.data.repository.internal;

import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.extension.spring.data.repository.specification.NativeQuerySpecification;

final class NativeQuerySpecificationQueryCreator implements SpecificationQueryCreator<NativeQuerySpecification, Query> {

  @Override
  public Query create(EntityManager entityManager,
                      NativeQuerySpecification specification, Class<?> domainClass) {
    Objects.requireNonNull(entityManager);
    Objects.requireNonNull(specification);

    final Query query = entityManager.createNativeQuery(specification.query());
    specification.withPredicate(query);
    return query;
  }
}
