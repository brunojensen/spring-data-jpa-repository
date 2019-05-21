# spring-data-repository

Specification repository API for complex quering... read again COMPLEX QUERING...

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

public interface PersonRepository extends RepositorySpecificationExecutor<Person, String> { }

```

```java
...

public List<Person> searchBy(final Person person) {
    return repository.searchBy(new TypedQuerySpecification<Person>() {
        @Override
        public String query() {
            return "FROM Person WHERE name = :name";
        }

        @Override
        public void withPredicate(Query query) {
            query.setParameter(“name”, “John”);
        }
    });
}

public long countBy(final Person person) {
    // it requires casting for lamba expressions.
    return repository.count((TypedQuerySpecification<Person>) () -> "SELECT count(*) FROM Person");
}

public Person findById(String id) {
    return repository.findBy(new CriteriaQuerySpecification<Person>() {
        @Override
        public Predicate toPredicate(Root<Person> r, CriteriaQuery<?> cq, CriteriaBuilder cb) {
            return cb.equal(r.get("email"), id);
        }
    });
}

...

```
