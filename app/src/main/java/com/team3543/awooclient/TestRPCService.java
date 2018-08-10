package com.team3543.awooclient;

import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcMethod;
import com.github.arteam.simplejsonrpc.core.annotation.JsonRpcService;

@JsonRpcService
public class TestRPCService
{
    @JsonRpcMethod("getMajiraInstance")
    public TestRPCClass getMajiraInstance()
    {
        return new TestRPCClass("tail", "fuzzball");
    }
}
