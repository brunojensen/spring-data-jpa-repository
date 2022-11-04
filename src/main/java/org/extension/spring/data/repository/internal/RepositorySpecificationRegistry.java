package org.extension.spring.data.repository.internal;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.extension.spring.data.repository.specification.NativeQuerySpecification;
import org.extension.spring.data.repository.specification.QuerySpecification;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;

final class RepositorySpecificationRegistry {

  private static final Map<Class<?>, Class<?>> registry = new LinkedHashMap<>();

  static {
    registry.put(TypedNativeQuerySpecification.class, TypedNativeQuerySpecificationProcessor.class);
    registry.put(TypedQuerySpecification.class, TypedQuerySpecificationProcessor.class);
    registry.put(NativeQuerySpecification.class, NativeQuerySpecificationProcessor.class);
    registry.put(QuerySpecification.class, QuerySpecificationProcessor.class);
  }

  private RepositorySpecificationRegistry() {
  }

  @SuppressWarnings("rawtypes")
  static SpecificationProcessor lookup(Class<?> specificationType) {
    try {
      return (SpecificationProcessor) registry.getOrDefault(specificationType,
        registry.entrySet().stream()
          .filter(entry -> entry.getKey().isAssignableFrom(specificationType))
          .findFirst()
          .map(Entry::getValue)
          .orElseThrow(() -> new IllegalArgumentException("undefined specification type"))).getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new UnsupportedOperationException(e);
    }
  }

}
