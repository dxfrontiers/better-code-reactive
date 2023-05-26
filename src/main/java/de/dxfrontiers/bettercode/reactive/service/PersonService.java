package de.dxfrontiers.bettercode.reactive.service;

import de.dxfrontiers.bettercode.reactive.exceptions.PersonNotFoundException;
import de.dxfrontiers.bettercode.reactive.model.Person;
import de.dxfrontiers.bettercode.reactive.persistence.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final HouseService houseService;

    public Flux<Person> findByLastName(String lastName) {
        return personRepository.findByLastName(lastName);
    }

    public Mono<Person> findById(Long id) {
        return personRepository.findById(id);
    }

    public Mono<Person> deleteByName(String firstName, String lastName) {
        return personRepository
                .findByFirstNameAndLastName(firstName, lastName)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new PersonNotFoundException(String.format("Person with name '%s %s' could not be resolved", firstName, lastName)))))
                .flatMap(person -> personRepository
                        .deleteById(person.getId())
                        .thenReturn(person));
    }

    public Mono<Person> addPerson(String firstName, String lastName) {
        return personRepository
                .existsByFirstNameAndLastName(firstName, lastName)
                .filter(exists -> !exists)
                .flatMap(notExists -> houseService
                        .findByName(lastName)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new PersonNotFoundException("")))))
                .flatMap(house -> personRepository.save(new Person()
                        .setFirstName(firstName)
                        .setLastName(lastName)
                        .setHouse(house.getId())));
    }
}
