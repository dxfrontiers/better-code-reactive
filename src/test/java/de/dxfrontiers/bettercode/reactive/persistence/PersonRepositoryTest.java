package de.dxfrontiers.bettercode.reactive.persistence;

import de.dxfrontiers.bettercode.reactive.model.House;
import de.dxfrontiers.bettercode.reactive.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@Import(DatabaseSchemaConfig.class)
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository cut;

    @Autowired
    private DatabaseClient databaseClient;

    @Test
    public void verifyExistsByFirstNameAndLastNameReturnsTrueForExistingEntry() {
        House stark = assembleHouse("Stark", "Winter is coming");
        Person sansa = assemblePerson("Sansa", "Stark");

        persistHouse(stark)
                .flatMap(houseId -> persistPerson(sansa.setHouse(houseId)))
                .subscribe();

        StepVerifier
                .create(cut.existsByFirstNameAndLastName("Sansa", "Stark"))
                .expectNext(true)
                .verifyComplete();
    }

    private Mono<Long> persistHouse(House house) {
        return databaseClient
                .sql("INSERT INTO houses (name, words) VALUES (:name, :words)")
                .filter((statement, executeFunction) -> statement.returnGeneratedValues("id").execute())
                .bind("name", house.getName())
                .bind("words", house.getWords())
                .fetch()
                .first()
                .map(r -> ((Integer) r.get("id")).longValue());
    }

    private Mono<Void> persistPerson(Person person) {
        return databaseClient
                .sql("INSERT INTO person (first_name, last_name, house) VALUES (:firstName, :lastName, :house)")
                .bind("firstName", person.getFirstName())
                .bind("lastName", person.getLastName())
                .bind("house", person.getHouse())
                .fetch()
                .first()
                .then();
    }

    private House assembleHouse(String name, String words) {
        return new House()
                .setName(name)
                .setWords(words);
    }

    private Person assemblePerson(String firstName, String lastName) {
        return new Person()
                .setFirstName(firstName)
                .setLastName(lastName);
    }

}
