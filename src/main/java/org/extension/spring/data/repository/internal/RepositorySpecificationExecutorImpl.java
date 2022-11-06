package org.extension.spring.data.repository.internal;

import org.extension.spring.data.repository.RepositorySpecificationExecutor;
import org.extension.spring.data.repository.specification.NativeQuerySpecification;
import org.extension.spring.data.repository.specification.QuerySpecification;
import org.extension.spring.data.repository.specification.Specification;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class RepositorySpecificationExecutorImpl<T, I extends Serializable>
  extends SimpleJpaRepository<T, I> implements RepositorySpecificationExecutor<T, I> {

  private final EntityManager entityManager;

  public RepositorySpecificationExecutorImpl(
    JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  public RepositorySpecificationExecutorImpl(Class<T> domainClass, EntityManager em) {
    super(domainClass, em);
    this.entityManager = em;
  }

  public T find(Specification specification) {
    return find(specification, getDomainClass());
  }

  public List<T> findAll(Specification specification) {
    return findAll(specification, getDomainClass());
  }

  public Page<T> findAll(Specification specification, Pageable pageable) {
    return findAll(specification, pageable, getDomainClass());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P> P find(Specification specification, Class<P> projectionType) {
    if (specification.isSatisfied()) {
      return (P) RepositorySpecificationRegistry.lookup(specification.getClass())
        .create(entityManager, specification, projectionType)
        .getSingleResult();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P> List<P> findAll(Specification specification, Class<P> projectionType) {
    if (specification.isSatisfied()) {
      return RepositorySpecificationRegistry.lookup(specification.getClass())
        .create(entityManager, specification, projectionType)
        .getResultList();
    }
    return Collections.emptyList();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public <P> Page<P> findAll(Specification specification, Pageable pageable, Class<P> projectionType) {
    if (specification.isSatisfied()) {
      long count = count(specification);
      if(count > 0) {
        return new PageImpl<>(
                RepositorySpecificationRegistry.lookup(specification.getClass())
                .create(entityManager, new SortQueryFor((QuerySpecification) specification, pageable.getSort()), projectionType)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList(),
                pageable,
                count);
      }
    }
    return new PageImpl<>(Collections.emptyList());
  }

  @SuppressWarnings("unchecked")
  public long count(Specification specification) {
    if (specification.isSatisfied()) {
      return ((Number) RepositorySpecificationRegistry.lookup(specification.getClass())
        .create(entityManager, new CountQueryFor((QuerySpecification) specification), null)
        .getSingleResult())
        .longValue();
    }
    return 0;
  }

  private record CountQueryFor(QuerySpecification querySpecification) implements QuerySpecification,
      NativeQuerySpecification, TypedQuerySpecification, TypedNativeQuerySpecification {

      // Spring marked org.springframework.data.jpa.repository.query.QueryUtils#createCountQueryFor as deprecated
      // but didn't provide any public implementation.
      @SuppressWarnings({"deprecation", "squid:CallToDeprecatedMethod"})
      @Override
      public String query() {
        return QueryUtils.createCountQueryFor(querySpecification.query(),
          "*".equals(QueryUtils.getProjection(querySpecification.query())) ? "1" : null);
      }

      @Override
      public boolean isSatisfied() {
        return querySpecification.isSatisfied();
      }

      @Override
      public void withPredicate(Query query) {
        querySpecification.withPredicate(query);
      }
    }

  private record SortQueryFor(QuerySpecification querySpecification, Sort sort) implements QuerySpecification,
      NativeQuerySpecification, TypedQuerySpecification, TypedNativeQuerySpecification {

    @Override
      public String query() {
        return QueryUtils.applySorting(querySpecification.query(), sort);
      }

      @Override
      public boolean isSatisfied() {
        return querySpecification.isSatisfied();
      }

      @Override
      public void withPredicate(Query query) {
        querySpecification.withPredicate(query);
      }
    }
}