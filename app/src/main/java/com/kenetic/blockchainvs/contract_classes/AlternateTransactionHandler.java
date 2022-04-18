package com.kenetic.blockchainvs.contract_classes;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class AlternateTransactionHandler {
    String TAG = "Test";

    public String registerVoteEncoded(Long num) throws IOException, ExecutionException, InterruptedException {
        Function function = new Function(
                "registerVote",
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(num)),
                Collections.<TypeReference<?>>emptyList());

        return FunctionEncoder.encode(function);
    }

    public String addMeToVotedListEncoded() throws IOException, ExecutionException, InterruptedException {
        Function function = new Function(
                "addMeToVotedList",
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return FunctionEncoder.encode(function);
    }
}
