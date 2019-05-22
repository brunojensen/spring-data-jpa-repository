# spring-data-jpa-repository

Specification repository API for complex queries by making use of Repository and Specification patterns. 

[![Build Status](https://travis-ci.org/brunojensen/spring-data-jpa-repository.svg?branch=master)](https://travis-ci.org/brunojensen/spring-data-jpa-repository)

Demo:
https://github.com/brunojensen/spring-data-repository-demo

Setup:

```java

@EnableJpaRepositories(
    value = "jpa.model.package",
    repositoryBaseClass = RepositorySpecificationExecutorImpl.class)
public class ... {

}

```

Usage:

```java

public interface PersonRepository extends RepositorySpecificationExecutor<Person, String> {
  // you still can use spring-data common Repository implementations
  // this framework is only giving you a boost
}

```

```java
...

public List<Person> searchBy(final Person person) {
    return repository.searchBy(new TypedQuerySpecification<Person>() {
        
        @Override
        public boolean isSatisfied() {
           return null != person && null != person.getName()
        }

        @Override
        public String query() {
            return "FROM Person WHERE name = :name";
        }

        @Override
        public void withPredicate(Query query) {
            query.setParameter(“name”, person.getName());
        }
    });
}

public long countAll() {
    // it requires casting for lamba expressions.
    return repository.count((QuerySpecification) () -> "SELECT count(*) FROM Person");
}

public Person findByEmail(String email) {
    return repository.findBy(new CriteriaQuerySpecification<Person>() {
        @Override
        public Predicate toPredicate(Root<Person> r, CriteriaQuery<?> cq, CriteriaBuilder cb) {
            return cb.equal(r.get("email"), email);
        }
    });
}

...

```
