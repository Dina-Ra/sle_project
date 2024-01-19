package ru.matrix.service;


import reactor.core.publisher.Mono;
import ru.matrix.domain.Matrix;

public interface DataStore {

    Mono<Matrix> saveMatrixDetail(Matrix matrix);

    Mono<Matrix> loadMatrixDetail(String strMatrix);
}
