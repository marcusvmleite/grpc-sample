package com.marcusvmleite.grpc.client;

import com.marcusvmleite.grpc.greet.*;
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

        GreetServiceGrpc.GreetServiceBlockingStub client = GreetServiceGrpc
                .newBlockingStub(managedChannel);

        //unaryRequest(client);
        serverStreamRequest(client);

        managedChannel.shutdown();
    }

    private static void serverStreamRequest(GreetServiceGrpc.GreetServiceBlockingStub client) {
        log.info("Sending request to gRPC stream server...");
        GreetStreamRequest request = GreetStreamRequest.newBuilder()
                .setGreeting(createGreet())
                .build();
        client.greetStream(request).forEachRemaining(result -> {
            log.info("Received stream response: " + result.getResult());
        });
    }

    private static void unaryRequest(GreetServiceGrpc.GreetServiceBlockingStub client) {
        log.info("Sending request to gRPC server...");
        GreetResponse response = client.greet(createGreetRequest());
        log.info("Got response: " + response.getResult());
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
