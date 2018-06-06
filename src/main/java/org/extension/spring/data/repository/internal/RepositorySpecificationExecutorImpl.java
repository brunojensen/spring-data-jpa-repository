package org.extension.spring.data.repository.internal;

import org.extension.spring.data.repository.RepositorySpecificationExecutor;
import org.extension.spring.data.repository.internal.enumeration.QueryType;
import org.extension.spring.data.repository.internal.specification.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.extension.spring.data.repository.specification.QuerySpecification;
import org.extension.spring.data.repository.specification.TypedNativeQuerySpecification;
import org.extension.spring.data.repository.specification.TypedQuerySpecification;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class RepositorySpecificationExecutorImpl<T, ID extends Serializable>
        extends SimpleJpaRepository<T, ID> implements RepositorySpecificationExecutor<T, ID> {

    private final EntityManager entityManager;

    public RepositorySpecificationExecutorImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    public RepositorySpecificationExecutorImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
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
            switch (specification.type()) {
                case QueryType.TYPED:
                    return TypedQuerySpecificationProcessor
                            .process(entityManager, projectionType, (TypedQuerySpecification<P>) specification)
                            .getSingleResult();
                case QueryType.TYPED_NATIVE:
                    return (P) TypedNativeQuerySpecificationProcessor
                            .process(entityManager, projectionType, (TypedNativeQuerySpecification<P>) specification)
                            .getSingleResult();
                case QueryType.CRITERIA:
                    return (P) super.findOne((org.springframework.data.jpa.domain.Specification<T>) specification);
                case QueryType.NATIVE:
                case QueryType.PLAIN:
                default:
                    throw new IllegalArgumentException(String.format(Constants.NOT_SUPPORTED, specification.type()));
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> List<P> findAll(Specification specification, Class<P> projectionType) {
        if (specification.isSatisfied()) {
            switch (specification.type()) {
                case QueryType.TYPED:
                    return TypedQuerySpecificationProcessor
                            .process(entityManager, projectionType, (TypedQuerySpecification<P>) specification)
                            .getResultList();
                case QueryType.CRITERIA:
                    return super.findAll((org.springframework.data.jpa.domain.Specification) specification);
                case QueryType.TYPED_NATIVE:
                    return TypedNativeQuerySpecificationProcessor
                            .process(entityManager, projectionType, (TypedNativeQuerySpecification<P>) specification)
                            .getResultList();
                case QueryType.NATIVE:
                case QueryType.PLAIN:
                default:
                    throw new IllegalArgumentException(String.format(Constants.NOT_SUPPORTED, specification.type()));
            }
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Page<P> findAll(Specification specification, Pageable pageable, Class<P> projectionType) {
        if (specification.isSatisfied()) {
            switch (specification.type()) {
                case QueryType.TYPED:
                    final TypedQuerySpecification<P> typedQuerySpecification = (TypedQuerySpecification<P>) specification;
                    return new PageImpl<>(
                            TypedQuerySpecificationProcessor
                                    .process(entityManager, projectionType, new SortQueryFor<>(typedQuerySpecification, pageable.getSort()))
                                    .setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList(), pageable, count(new CountQueryFor(typedQuerySpecification)));
                case QueryType.TYPED_NATIVE:
                    final TypedNativeQuerySpecification<P> typedNativeQuerySpecification = (TypedNativeQuerySpecification<P>) specification;
                    return new PageImpl<>(
                            TypedNativeQuerySpecificationProcessor
                                    .process(entityManager, projectionType, typedNativeQuerySpecification)
                                    .setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList(), pageable, count(new CountQueryFor(typedNativeQuerySpecification)));
                case QueryType.CRITERIA:
                    return findAll((org.springframework.data.jpa.domain.Specification) specification, pageable);
                case QueryType.PLAIN:
                case QueryType.NATIVE:
                default:
                    throw new IllegalArgumentException(String.format(Constants.NOT_SUPPORTED, specification.type()));
            }
        }
        return new PageImpl<>(Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public long count(Specification specification) {
        if (specification.isSatisfied()) {
            switch (specification.type()) {
                case QueryType.PLAIN:
                case QueryType.TYPED:
                case QueryType.TYPED_NATIVE:
                    return ((Number) QuerySpecificationProcessor
                            .process(entityManager, new CountQueryFor((QuerySpecification) specification))
                            .getSingleResult())
                            .longValue();
                case QueryType.NATIVE:
                    return ((Number) NativeQuerySpecificationProcessor
                            .process(entityManager, new CountQueryFor((QuerySpecification) specification))
                            .getSingleResult())
                            .longValue();
                case QueryType.CRITERIA:
                default:
                    return count((org.springframework.data.jpa.domain.Specification) specification);
            }
        }
        return 0;
    }

    private static final class CountQueryFor implements QuerySpecification {

        private final QuerySpecification querySpecification;

        CountQueryFor(final QuerySpecification querySpecification) {
            this.querySpecification = querySpecification;
        }

        @SuppressWarnings("deprecation")
        @Override
        public String query() {
            return QueryUtils.createCountQueryFor(querySpecification.query());
        }

        @Override
        public void toPredicate(Query query) {
            querySpecification.toPredicate(query);
        }
    }

    private static final class SortQueryFor<T> implements TypedQuerySpecification<T> {

        private final TypedQuerySpecification<T> typedQuerySpecification;
        private final Sort sort;


        SortQueryFor(final TypedQuerySpecification<T> typedQuerySpecification, final Sort sort) {
            this.typedQuerySpecification = typedQuerySpecification;
            this.sort = sort;
        }

        @Override
        public String query() {
            return QueryUtils.applySorting(typedQuerySpecification.query(), sort);
        }

        @Override
        public void toPredicate(Query query) {
            typedQuerySpecification.toPredicate(query);
        }
    }
}