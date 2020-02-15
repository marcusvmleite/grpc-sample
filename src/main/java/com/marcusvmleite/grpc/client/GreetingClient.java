package com.marcusvmleite.grpc.client;

import com.marcusvmleite.grpc.dummy.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {

    public static void main(String[] args) {

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext() //disabling TLS for dev. purposes only (DO NOT USE IN PROD!)
                .build();

        DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc
                .newBlockingStub(managedChannel);

        //do something (API call)

        managedChannel.shutdown();
    }

}
