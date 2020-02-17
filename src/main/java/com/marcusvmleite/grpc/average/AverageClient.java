package com.marcusvmleite.grpc.average;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class AverageClient {

    private static final Logger log = LogManager.getLogger(AverageClient.class);

    public static void main(String[] args) throws InterruptedException {

        log.info("Starting gRPC client...");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() //disabling TLS for dev. purposes only (DO NOT USE IN PROD!)
                .build();

        log.info("Sending request to gRPC server...");

        AverageServiceGrpc.AverageServiceStub client = AverageServiceGrpc.newStub(managedChannel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> observer = client.averageOperation(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse averageResponse) {
                log.info("Received from Server after Client Streaming: " + averageResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                log.error("An error occurred.", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Server finished sending data after Client Streaming.");
                latch.countDown();
            }
        });

        IntStream.rangeClosed(1, 5).forEach(i -> observer.onNext(AverageRequest.newBuilder()
                .setNumber(i)
                .build()));

        //As on this example the server will send back a response only
        //when the client is done sending data, we need to tell the server
        //that the client is done.
        observer.onCompleted();

        latch.await(5L, TimeUnit.SECONDS);

        managedChannel.shutdown();
    }

}
