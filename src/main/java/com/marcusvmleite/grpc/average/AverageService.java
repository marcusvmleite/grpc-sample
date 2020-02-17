package com.marcusvmleite.grpc.average;

import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AverageService extends AverageServiceGrpc.AverageServiceImplBase {

    private static final Logger log = LogManager.getLogger(AverageService.class);

    public StreamObserver<AverageRequest> averageOperation(StreamObserver<AverageResponse> responseObserver) {
        return new StreamObserver<AverageRequest>() {

            private List<Integer> incoming = new ArrayList<>();

            @Override
            public void onNext(AverageRequest averageRequest) {
                incoming.add(averageRequest.getNumber());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("An error occurred.", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Got info from Stream Client that it has completed sending data!");
                responseObserver.onNext(AverageResponse.newBuilder()
                        .setResult(incoming.stream().mapToInt(i -> i).average().orElse(0D))
                        .build());
            }
        };
    }

}
