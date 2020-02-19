package com.marcusvmleite.grpc.greet;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    private static final Logger log = LogManager.getLogger(GreetingClient.class);

    public static void main(String[] args) throws InterruptedException {

        log.info("Starting gRPC client...");

        ManagedChannel managedChannel = getManagedChannel();

        unaryRequest(getSyncClient());
        serverStreamRequest(getSyncClient());
        clientStreamRequest(getAsyncClient());
        biDirectionalStream(getAsyncClient());

        managedChannel.shutdown();
    }

    private static GreetServiceGrpc.GreetServiceBlockingStub getSyncClient() {
        return GreetServiceGrpc.newBlockingStub(getManagedChannel());
    }

    private static ManagedChannel getManagedChannel() {
        return ManagedChannelBuilder
                    .forAddress("localhost", 50051)
                    .usePlaintext() //disabling TLS for dev. purposes only (DO NOT USE IN PROD!)
                    .build();
    }

    private static ManagedChannel getSSLManagedChannel() throws SSLException {
        return NettyChannelBuilder.forAddress("localhost", 443)
                .sslContext(GrpcSslContexts.forClient()
                        .trustManager(new File("roots.pem")).build())
                .build();
    }

    private static GreetServiceGrpc.GreetServiceStub getAsyncClient() {
        return GreetServiceGrpc.newStub(getManagedChannel());
    }

    private static void clientStreamRequest(GreetServiceGrpc.GreetServiceStub client) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> observer = client.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse longGreetResponse) {
                //Get response from server
                log.info("Received from Server after Client Streaming: " + longGreetResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                //Server sends an error
                log.error("An error occurred.", throwable);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                //Server finished sending data
                log.info("Server finished sending data after Client Streaming.");
                latch.countDown();
            }
        });

        Arrays.asList("Marcus", "Dielle", "Mayla").forEach(name -> {
            System.out.println("Sending Greet Request for: " + name);
            observer.onNext(LongGreetRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder()
                            .setFirstName(name)
                            .build())
                    .build());
        });

        //As on this example the server will send back a response only
        //when the client is done sending data, we need to tell the server
        //that the client is done.
        observer.onCompleted();

        latch.await(5L, TimeUnit.SECONDS);
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

    private static void biDirectionalStream(GreetServiceGrpc.GreetServiceStub asyncClient) throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> observer = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse greetEveryoneResponse) {
                //Get response from server
                log.info("Received from Server after Client Streaming: " + greetEveryoneResponse.getResult());
            }

            @Override
            public void onError(Throwable throwable) {
                //Server sends an error
                log.error("An error occurred.", throwable);
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                //Server finished sending data
                log.info("Server finished sending data after Client Streaming.");
                latch.countDown();
            }
        });

        Arrays.asList("Marcus", "Dielle", "Mayla").forEach(name -> {
            System.out.println("Sending Greet Request for: " + name);
            observer.onNext(GreetEveryoneRequest.newBuilder()
                    .setGreeting(Greeting.newBuilder()
                            .setFirstName(name)
                            .build())
                    .build());
        });

        observer.onCompleted();

        latch.await(5L, TimeUnit.SECONDS);
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
