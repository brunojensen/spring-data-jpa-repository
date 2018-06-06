package com.bea.spring.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface RepositorySpecificationExecutor<T, ID extends Serializable>
        extends Repository<T, ID> {

    public T findBy(DefaultSpecification<T> specification);

    public List<T> searchBy(DefaultSpecification<T> specification);

    long count(DefaultSpecification<T> specification);

}