package com.marcusvmleite.grpc.sum;

import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SumService extends SumServiceGrpc.SumServiceImplBase {

    private static final Logger log = LogManager.getLogger(SumService.class);

    @Override
    public void sum(SumRequest request,
                      StreamObserver<SumResponse> responseObserver) {

        log.info("Server received request on SumService!");

        Sum sum = request.getSum();

        String content = sum.getFirst() + " + " + sum.getSecond();

        log.info("Content of request is: " + content);

        SumResponse response = SumResponse.newBuilder()
                .setResult(sum.getFirst() + sum.getSecond())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
