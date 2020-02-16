package com.marcusvmleite.grpc.server;

import com.marcusvmleite.grpc.greet.GreetRequest;
import com.marcusvmleite.grpc.greet.GreetResponse;
import com.marcusvmleite.grpc.greet.GreetServiceGrpc;
import com.marcusvmleite.grpc.greet.Greeting;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GreetService extends GreetServiceGrpc.GreetServiceImplBase {

    private static final Logger log = LogManager.getLogger(GreetService.class);

    @Override
    public void greet(GreetRequest request,
                      StreamObserver<GreetResponse> responseObserver) {

        log.info("Server received request on GreetService!");

        Greeting greeting = request.getGreeting();

        String content = greeting.getFirstName() + " " + greeting.getLastName();

        log.info("Content of request is: " + content);

        GreetResponse response = GreetResponse.newBuilder()
                .setResult("Sending back for request " + content)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
