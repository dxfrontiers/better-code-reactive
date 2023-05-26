package de.dxfrontiers.bettercode.reactive.persistence;

import de.dxfrontiers.bettercode.reactive.model.House;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface HouseRepository extends ReactiveCrudRepository<House, Long> {

    Mono<House> findByName(String name);

}
