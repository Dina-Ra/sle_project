package ru.matrix.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.matrix.domain.Matrix;

public interface MatrixDetailRepository extends ReactiveCrudRepository<Matrix, Long> {

    @Query("select * from matrix where str_matrix = :str_matrix")
    Mono<Matrix> findByStrMatrix(@Param("strMatrix") String strMatrix);
}
