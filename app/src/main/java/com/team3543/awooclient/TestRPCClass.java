package com.team3543.awooclient;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;

@JsonRpcService
public class TestRPCClass
{
    @JsonProperty
    private String firstName;
    private String lastName;
    private long timeInitialized = 0;


    @JsonCreator
    public TestRPCClass()
    {
        firstName = "Tail";
        lastName = "Fuzzball";
    }

    @JsonCreator
    public TestRPCClass(@JsonProperty("firstName") String firstName, @JsonProperty("lastName") String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @JsonRpcMethod
    public void initTime()
    {
        Date date = new Date();
        timeInitialized = date.getTime();
    }

    @Override
    @JsonRpcMethod
    public String toString()
    {
        return firstName + " " + lastName + " " + timeInitialized;
    }
}
