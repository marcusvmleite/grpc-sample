package com.marcusvmleite.grpc.server;

import com.marcusvmleite.grpc.sum.Sum;
import com.marcusvmleite.grpc.sum.SumRequest;
import com.marcusvmleite.grpc.sum.SumResponse;
import com.marcusvmleite.grpc.sum.SumServiceGrpc;
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
