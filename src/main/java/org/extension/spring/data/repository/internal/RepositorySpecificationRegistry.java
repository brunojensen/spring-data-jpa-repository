package org.extension.spring.data.repository.internal;

import java.util.Map;
import java.util.Map.Entry;

import org.extension.spring.data.repository.specification.NativeQuerySpecification;
import org.extension.spring.data.repository.specification.QuerySpecification;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;

final class RepositorySpecificationRegistry {

  private static final Map<Class<?>, Class<?>> registry = Map.ofEntries(
    Map.entry(TypedNativeQuerySpecification.class, TypedNativeQuerySpecificationQueryCreator.class),
    Map.entry(TypedQuerySpecification.class, TypedQuerySpecificationQueryCreator.class),
    Map.entry(NativeQuerySpecification.class, NativeQuerySpecificationQueryCreator.class),
    Map.entry(QuerySpecification.class, QuerySpecificationQueryCreator.class)
  );

  private RepositorySpecificationRegistry() {
  }

  @SuppressWarnings("unchecked")
  static <T> SpecificationQueryCreator<T, ?> lookup(Class<?> specificationType) {
    try {
      return (SpecificationQueryCreator<T, ?>) registry.getOrDefault(specificationType, internalLookup(specificationType))
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
