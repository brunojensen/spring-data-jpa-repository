package org.extension.spring.data.repository.specification;

import org.extension.spring.data.repository.internal.enumeration.QueryType;

/**
 * Specification to typed native queries.
 *
 * <p>Can be used with ResultSetMapping by annotation the DTO with {@link org.extension.spring.data.repository.annotations.TypedAsSqlResultSetMapping}
 * or with the JPA entity.
 * </p>
 */
public interface TypedNativeQuerySpecification extends QuerySpecification {

    /**
     * Don't override me.
     *
     * @return in case of {@link TypedNativeQuerySpecification} must return TYPED_NATIVE
     */
    @Override
    default QueryType type() {
        return QueryType.TYPED_NATIVE;
    }
}
