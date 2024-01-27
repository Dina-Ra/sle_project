package ru.matrix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import ru.matrix.domain.Matrix;
import ru.matrix.repository.MatrixDetailRepository;

@Service
public class DataStoreR2dbc implements DataStore {
    private static final Logger log = LoggerFactory.getLogger(DataStoreR2dbc.class);
    private final MatrixDetailRepository matrixDetailRepository;

    public DataStoreR2dbc(MatrixDetailRepository matrixDetailRepository) {
        this.matrixDetailRepository = matrixDetailRepository;
    }

    @Override
    public Mono<Matrix> saveMatrixDetail(Matrix matrix) {
        log.info("save matrix detail:{}", matrix);
        return matrixDetailRepository.save(matrix);
    }

    @Override
    public Mono<Matrix> loadMatrixDetail(String strMatrix) {
        log.info("load matrix by strMatrix: {}", strMatrix);
        return matrixDetailRepository.findByStrMatrix(strMatrix);
    }
}