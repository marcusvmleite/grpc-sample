package com.marcusvmleite.grpc.sum;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SumClient {

    private static final Logger log = LogManager.getLogger(SumClient.class);

    public static void main(String[] args) {

        log.info("Starting gRPC client...");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() //disabling TLS for dev. purposes only (DO NOT USE IN PROD!)
                .build();

        log.info("Sending request to gRPC server...");

        SumServiceGrpc.SumServiceBlockingStub client = SumServiceGrpc.newBlockingStub(managedChannel);
        SumResponse response = client.sum(createSumRequest());

        log.info("Got response: " + response.getResult());

        managedChannel.shutdown();
    }

    private static SumRequest createSumRequest() {
        return SumRequest.newBuilder()
                .setSum(createSum())
                .build();
    }

    private static Sum createSum() {
        return Sum.newBuilder()
                .setFirst(1)
                .setSecond(2)
                .build();
    }

}
