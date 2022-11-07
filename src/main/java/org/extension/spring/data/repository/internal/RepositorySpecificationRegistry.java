package org.extension.spring.data.repository.internal;

import java.util.Map;
import java.util.Map.Entry;

import org.extension.spring.data.repository.specification.NativeQuerySpecification;
import org.extension.spring.data.repository.specification.QuerySpecification;
import org.extension.spring.data.repository.specification.Specification;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;

final class RepositorySpecificationRegistry {

  private static final Map<Class<?>, Class<?>> registry = Map.of(
    TypedQuerySpecification.class, TypedQuerySpecificationQueryCreator.class,
    TypedNativeQuerySpecification.class, TypedNativeQuerySpecificationQueryCreator.class,
    NativeQuerySpecification.class, NativeQuerySpecificationQueryCreator.class,
    QuerySpecification.class, QuerySpecificationQueryCreator.class
  );

  private RepositorySpecificationRegistry() {
  }

  @SuppressWarnings("unchecked")
  static <T> SpecificationQueryCreator<T, ?> lookup(Class<?> specificationType) {
    try {
      return (SpecificationQueryCreator<T, ?>) internalLookup(specificationType)
                                                    .getDeclaredConstructor()
                                                    .newInstance();
    } catch (Exception e) {
      throw new UnsupportedOperationException(e);
    }
  }

  private static Class<?> internalLookup(Class<?> specificationType) {
    return registry.entrySet().stream()
      .filter(entry -> entry.getKey().isAssignableFrom(specificationType))
      .findFirst()
      .map(Entry::getValue)
      .orElseThrow(() -> new IllegalArgumentException("Illegal specification type"));
  }

}
