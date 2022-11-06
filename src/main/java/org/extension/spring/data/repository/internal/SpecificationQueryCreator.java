package org.extension.spring.data.repository.internal;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public sealed interface SpecificationQueryCreator<T, Q extends Query>
  permits NativeQuerySpecificationQueryCreator, QuerySpecificationQueryCreator, TypedNativeQuerySpecificationQueryCreator, TypedQuerySpecificationQueryCreator {

  Q create(EntityManager entityManager, T specification, Class<?> domainClass);
}
