package com.marcusvmleite.grpc.greet;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class GreetingServer {

    private static final Logger log = LogManager.getLogger(GreetingServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        log.info("Starting gRPC server...");

        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetService())
                .addService(ProtoReflectionService.newInstance()) //Service Reflection
                .build();

        Server securedServer = ServerBuilder.forPort(8443)
                // Enable TLS
                .useTransportSecurity(new File("server.crt"), new File("server.pem"))
                .addService(new GreetService())
                .build();

        server.start();

        log.info("gRPC up and running!");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Received shutdown request.");
            server.shutdown();
            log.info("Successfully stopped server.");
        }));

        server.awaitTermination();
    }

}
