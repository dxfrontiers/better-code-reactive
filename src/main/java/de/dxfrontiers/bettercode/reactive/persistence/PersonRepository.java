package de.dxfrontiers.bettercode.reactive.persistence;

import de.dxfrontiers.bettercode.reactive.model.Person;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {

    Flux<Person> findByLastName(String lastName);
    Mono<Person> findByFirstNameAndLastName(String firstName, String lastName);
    Mono<Boolean> existsByFirstNameAndLastName(String firstName, String lastName);

}
