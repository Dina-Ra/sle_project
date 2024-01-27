package ru.matrix.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.util.HtmlUtils;
import reactor.core.publisher.Mono;
import ru.matrix.domain.Matrix;


@Controller
public class MatrixController {
    private static final Logger logger = LoggerFactory.getLogger(MatrixController.class);

    private static final String TOPIC_TEMPLATE = "/topic/response.";

    private final WebClient datastoreClient;
    private final SimpMessagingTemplate template;

    public MatrixController(WebClient datastoreClient, SimpMessagingTemplate template) {
        this.datastoreClient = datastoreClient;
        this.template = template;
    }

    @MessageMapping("/matrix.{strMatrix}")
    public void getMessage(@DestinationVariable String strMatrix) {
        logger.info("strMatrix:{}", strMatrix);
        if (strMatrix == null) {
            logger.error("strMatrix cannot be null");
            throw new MatrixDetailException("strMatrix cannot be null");
        }

        try {

            saveMatrixDetail(strMatrix)
                    .subscribe(matrix -> template.convertAndSend(String.format("%s%s", TOPIC_TEMPLATE, strMatrix),  new Matrix(
                            HtmlUtils.htmlEscape(matrix.strMatrix()),
                            HtmlUtils.htmlEscape(matrix.det()),
                            HtmlUtils.htmlEscape(matrix.reverse()),
                            HtmlUtils.htmlEscape(matrix.transpose()),
                            HtmlUtils.htmlEscape(matrix.decomposition())
                    )));

//            template.convertAndSend(String.format("%s%s", TOPIC_TEMPLATE, strMatrix),
//                    new Matrix(
//                            HtmlUtils.htmlEscape(matrixDto.strMatrix()),
//                            HtmlUtils.htmlEscape(matrixDto.det()),
//                            HtmlUtils.htmlEscape(matrixDto.reverse()),
//                            HtmlUtils.htmlEscape(matrixDto.transpose()),
//                            HtmlUtils.htmlEscape(matrixDto.decomposition())
//                    ));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new MatrixDetailException(e.getMessage());
        }
    }

    @EventListener
    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
        var genericMessage = (GenericMessage<byte[]>) event.getMessage();
        var simpDestination = (String) genericMessage.getHeaders().get("simpDestination");

        if (simpDestination == null) {
            logger.error("Can not get simpDestination header, headers:{}", genericMessage.getHeaders());
            throw new MatrixDetailException("Can not get simpDestination header");
        }

        String strMatrix = simpDestination.replace(TOPIC_TEMPLATE, "");

        getMatrixDetailByStrMatrix(strMatrix)
                .doOnError(ex -> logger.error("getting matrix details for matrixId:{} failed", strMatrix, ex))
                .subscribe(matrix -> template.convertAndSend(simpDestination, matrix));
    }

    private Mono<Matrix> saveMatrixDetail(String strMatrix) {
        return datastoreClient.post().uri(String.format("/matrix/%s", strMatrix))
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> response.bodyToMono(Matrix.class));
    }

    private Mono<Matrix> getMatrixDetailByStrMatrix(String strMatrix) {
        return datastoreClient.get().uri(String.format("/matrix/%s", strMatrix))
                .accept(MediaType.APPLICATION_NDJSON)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.OK)) {
                        return response.bodyToMono(Matrix.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }
}
