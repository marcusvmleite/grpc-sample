package com.marcusvmleite.grpc.max;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MaxServer {

    private static final Logger log = LogManager.getLogger(MaxServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        log.info("Starting gRPC server...");

        Server server = ServerBuilder.forPort(50051)
                .addService(new MaxService())
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
