package com.marcusvmleite.grpc.max;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxService extends MaxServiceGrpc.MaxServiceImplBase {

    private static final Logger log = LogManager.getLogger(MaxService.class);

    @Override
    public StreamObserver<MaxRequest> maxFinder(StreamObserver<MaxResponse> responseObserver) {
        return new StreamObserver<MaxRequest>() {

            private int max = Integer.MIN_VALUE;

            @Override
            public void onNext(MaxRequest maxRequest) {
                int input = maxRequest.getNumber();
                if (input == Integer.MIN_VALUE) {
                    responseObserver.onError(
                            Status.INVALID_ARGUMENT
                                .withDescription("Provided input is too small...")
                                .augmentDescription("Provided input is: " + input)
                                .asRuntimeException())
                    ;
                } else if (input > max) {
                    max = input;
                    responseObserver.onNext(MaxResponse.newBuilder()
                            .setMax(max)
                            .build());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("An error occurred.", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Got info from Stream Client that it has completed sending data!");
                responseObserver.onCompleted();
            }
        };
    }
}
