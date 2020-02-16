package com.marcusvmleite.grpc.client;

import com.marcusvmleite.grpc.greet.GreetRequest;
import com.marcusvmleite.grpc.greet.GreetResponse;
import com.marcusvmleite.grpc.greet.GreetServiceGrpc;
import com.marcusvmleite.grpc.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GreetingClient {

    private static final Logger log = LogManager.getLogger(GreetingClient.class);

    public static void main(String[] args) {

        log.info("Starting gRPC client...");

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() //disabling TLS for dev. purposes only (DO NOT USE IN PROD!)
                .build();

        log.info("Sending request to gRPC server...");

        GreetServiceGrpc.GreetServiceBlockingStub client = GreetServiceGrpc.newBlockingStub(managedChannel);
        GreetResponse response = client.greet(createGreetRequest());

        log.info("Got response: " + response.getResult());

        managedChannel.shutdown();
    }

    private static GreetRequest createGreetRequest() {
        return GreetRequest.newBuilder()
                .setGreeting(createGreet())
                .build();
    }

    private static Greeting createGreet() {
        return Greeting.newBuilder()
                .setFirstName("Aragorn")
                .setLastName("Elessar")
                .build();
    }

}
