package com.marcusvmleite.grpc.max;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MaxClient {

    private static final Logger log = LogManager.getLogger(MaxClient.class);

    public static void main(String[] args) throws InterruptedException {

        log.info("Starting gRPC client...");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() //disabling TLS for dev. purposes only (DO NOT USE IN PROD!)
                .build();

        log.info("Sending request to gRPC server...");

        MaxServiceGrpc.MaxServiceStub client = MaxServiceGrpc.newStub(managedChannel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<MaxRequest> observer = client.maxFinder(new StreamObserver<MaxResponse>() {
            @Override
            public void onNext(MaxResponse averageResponse) {
                log.info("Received from Server after Client Streaming: " + averageResponse.getMax());
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

        List<Integer> input = Arrays.asList(1, 5, 2, 10, 3, 50, 1000, 900, 850, 1001, Integer.MIN_VALUE);

        input.stream().mapToInt(i -> i).forEach(i -> observer.onNext(MaxRequest.newBuilder()
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
