package ru.matrix.api;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
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
    private final WebClient calculatorClient;
    private final Scheduler workerPool;

    public DataController(DataStore dataStore, WebClient calculatorClient, Scheduler workerPool) {
        this.dataStore = dataStore;
        this.calculatorClient = calculatorClient;
        this.workerPool = workerPool;
    }

    @PostMapping(value = "/matrix/{strMatrix}")
    public Mono<MatrixDto> messageFromChat(@PathVariable("strMatrix") String strMatrix) {

        Mono<MatrixDto> matrixDto = getMatrixDetails(strMatrix);
        log.info("matrix calculate");

        return matrixDto
                .map(matrix -> new Matrix(strMatrix, matrix.det(), matrix.reverse(), matrix.transpose(), matrix.decomposition()))
                .doOnNext(matrix -> log.info("matrix by save :{}", matrix.getStrMatrix()))
                .flatMap(dataStore::saveMatrixDetail)
                .publishOn(workerPool)
                .doOnNext(matrixSaved -> log.info("matrixSaved id:{}", matrixSaved.getId()))
                .map(matrix -> new MatrixDto(matrix.getStrMatrix(), matrix.getDet(), matrix.getReverse(), matrix.getTranspose(), matrix.getDecomposition()))
                .subscribeOn(workerPool);
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

    private Mono<MatrixDto> getMatrixDetails(String strMatrix) {
        return calculatorClient.get().uri(String.format("/matrix/%s", strMatrix))
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(MatrixDto.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }
}
