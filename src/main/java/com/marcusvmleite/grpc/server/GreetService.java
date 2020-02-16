package com.marcusvmleite.grpc.server;

import com.marcusvmleite.grpc.greet.*;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.IntStream;

public class GreetService extends GreetServiceGrpc.GreetServiceImplBase {

    private static final Logger log = LogManager.getLogger(GreetService.class);

    @Override
    public void greet(GreetRequest request,
                      StreamObserver<GreetResponse> responseObserver) {

        log.info("Server received Unary request on GreetService!");

        Greeting greeting = request.getGreeting();
        String content = greeting.getFirstName() + " " + greeting.getLastName();

        log.info("Content of request is: " + content);

        GreetResponse response = GreetResponse.newBuilder()
                .setResult("Sending back for Unary request " + content)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    public void greetStream(GreetStreamRequest request,
                            StreamObserver<GreetStreamResponse> responseObserver) {

        log.info("Server received Stream request on GreetService!");

        Greeting greeting = request.getGreeting();
        String content = greeting.getFirstName() + " " + greeting.getLastName();

        log.info("Content of request is: " + content);

        IntStream.rangeClosed(0, 10).forEach(i -> {
            GreetStreamResponse response = GreetStreamResponse.newBuilder()
                    .setResult("Sending back for Stream request " + content)
                    .build();
            responseObserver.onNext(response);
            //Simulating a delay for testing the stream
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        responseObserver.onCompleted();
    }

}
