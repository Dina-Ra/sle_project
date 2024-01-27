package matrix.api;


import matrix.creator.MatrixCreateService;
import matrix.domain.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


@RestController
public class CalculatorController {
    private static final Logger log = LoggerFactory.getLogger(CalculatorController.class);
    private final Scheduler workerPool;
    private final MatrixCreateService matrixCreateService;

    public CalculatorController(Scheduler workerPool, MatrixCreateService matrixCreateService) {
        this.workerPool = workerPool;
        this.matrixCreateService = matrixCreateService;
    }

    @GetMapping(value = "/matrix/{strMatrix}", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<Matrix> getMessagesByRoomId(@PathVariable("strMatrix") String strMatrix) {
        try {
            return Mono.just(matrixCreateService.getMatrixDetail(strMatrix))
                    .doOnError(ex -> log.error(ex.getMessage()))
                    .doOnNext(matrix -> log.info("matrix:{}", matrix))
                    .subscribeOn(workerPool);
        } catch (Exception e) {
            log.error("strMatrix - {}, error - {}", strMatrix, e.getMessage());
            return Mono.empty();
        }
    }
}
