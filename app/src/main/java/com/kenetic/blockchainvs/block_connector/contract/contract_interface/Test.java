package com.kenetic.blockchainvs.block_connector.contract.contract_interface;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class Test {
    String TAG = "Test";

    public String testFunction(Long num) throws IOException, ExecutionException, InterruptedException {
        Function function = new Function(
                "registerVote",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(num)),
                Collections.<TypeReference<?>>emptyList());

        String encodedFunction = FunctionEncoder.encode(function);
        return encodedFunction;
    }
}
