package org.extension.spring.data.repository.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
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
          .process(entityManager, specification, projectionType)
          .getSingleResult();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P> List<P> findAll(Specification specification, Class<P> projectionType) {
    if (specification.isSatisfied()) {
      return RepositorySpecificationRegistry.lookup(specification.getClass())
          .process(entityManager, specification, projectionType)
          .getResultList();
    }
    return Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <P> Page<P> findAll(Specification specification, Pageable pageable, Class<P> projectionType) {
    if (specification.isSatisfied()) {
      return new PageImpl<>(
          RepositorySpecificationRegistry
              .lookup(specification.getClass())
              .process(entityManager, new SortQueryFor((QuerySpecification) specification, pageable.getSort()) , projectionType)
              .setFirstResult((int) pageable.getOffset())
              .setMaxResults(pageable.getPageSize())
              .getResultList(),
          pageable,
          count(specification));
    }
    return new PageImpl<>(Collections.emptyList());
  }

  @SuppressWarnings("unchecked")
  public long count(Specification specification) {
    if (specification.isSatisfied()) {
      return ((Number) RepositorySpecificationRegistry
                        .lookup(specification.getClass())
                        .process(entityManager, new CountQueryFor((QuerySpecification) specification), null)
                        .getSingleResult())
              .longValue();
    }
    return 0;
  }

  private static final class CountQueryFor implements QuerySpecification,
      NativeQuerySpecification, TypedQuerySpecification, TypedNativeQuerySpecification {

    private final QuerySpecification querySpecification;

    CountQueryFor(final QuerySpecification querySpecification) {
      this.querySpecification = querySpecification;
    }

    // Spring mark a deprecation on the org.springframework.data.jpa.repository.query.QueryUtils#createCountQueryFor
    // but didn't provide any public implementation alternative.
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

  private static final class SortQueryFor implements QuerySpecification,
      NativeQuerySpecification, TypedQuerySpecification, TypedNativeQuerySpecification {

    private final QuerySpecification querySpecification;
    private final Sort sort;

    SortQueryFor(final QuerySpecification querySpecification, final Sort sort) {
      this.querySpecification = querySpecification;
      this.sort = sort;
    }

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