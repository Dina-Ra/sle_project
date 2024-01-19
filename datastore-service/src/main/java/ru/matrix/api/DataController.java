package ru.matrix.api;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import ru.matrix.domain.Matrix;
import ru.matrix.domain.MatrixDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.matrix.service.DataStore;


@RestController
public class DataController {
    private static final Logger log = LoggerFactory.getLogger(DataController.class);
    private final DataStore dataStore;
    private final Scheduler workerPool;

    public DataController(DataStore dataStore, Scheduler workerPool) {
        this.dataStore = dataStore;
        this.workerPool = workerPool;
    }

    @PostMapping(value = "/matrix/{strMatrix}")
    public Mono<Long> messageFromChat(@PathVariable("strMatrix") String strMatrix,
                                      @RequestBody MatrixDto matrixDto) {

        var matrixId = Mono.just(new Matrix(strMatrix, matrixDto.det(), matrixDto.reverse(), matrixDto.transpose(), matrixDto.decomposition()))
                .doOnNext(matrix -> log.info("matrix by save :{}", matrix.getStrMatrix()))
                .flatMap(dataStore::saveMatrixDetail)
                .publishOn(workerPool)
                .doOnNext(matrixSaved -> log.info("matrixSaved id:{}", matrixSaved.getId()))
                .map(Matrix::getId)
                .subscribeOn(workerPool);

        log.info("matrixDto: {}", matrixDto);
        return matrixId;
    }

    @GetMapping(value = "/matrix/{strMatrix}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<MatrixDto> getMessagesByRoomId(@PathVariable("strMatrix") String strMatrix) {
        return Mono.just(strMatrix)
                .doOnNext(matrixString -> log.info("getMatrixDetailByStrMatrix, matrixString:{}", matrixString))
                .flatMap(dataStore::loadMatrixDetail)
                .map(matrix -> new MatrixDto(matrix.getStrMatrix(), matrix.getDet(), matrix.getReverse(), matrix.getTranspose(), matrix.getDecomposition()))
                .doOnNext(matrixDto -> log.info("matrixDto:{}", matrixDto))
                .subscribeOn(workerPool);
    }

}
