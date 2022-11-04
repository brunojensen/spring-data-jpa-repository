package org.extension.spring.data.repository.internal;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public sealed interface SpecificationProcessor<T, Q extends Query>
  permits NativeQuerySpecificationProcessor, QuerySpecificationProcessor, TypedNativeQuerySpecificationProcessor, TypedQuerySpecificationProcessor {

  Q process(EntityManager entityManager, T specification, Class<?> domainClass);
}
