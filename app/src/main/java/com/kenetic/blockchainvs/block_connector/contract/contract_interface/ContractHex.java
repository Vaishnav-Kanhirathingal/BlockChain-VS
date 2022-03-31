package com.kenetic.blockchainvs.block_connector.contract.contract_interface;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class ContractHex extends Contract {
    public static final String BINARY = "{\n"
            + "\t\"functionDebugData\": {\n"
            + "\t\t\"@_30\": {\n"
            + "\t\t\t\"entryPoint\": null,\n"
            + "\t\t\t\"id\": 30,\n"
            + "\t\t\t\"parameterSlots\": 0,\n"
            + "\t\t\t\"returnSlots\": 0\n"
            + "\t\t}\n"
            + "\t},\n"
            + "\t\"generatedSources\": [],\n"
            + "\t\"linkReferences\": {},\n"
            + "\t\"object\": \"608060405234801561001057600080fd5b506000600281905560038190556004556103be8061002f6000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063449b6db21161005b578063449b6db2146100b8578063464d4a92146100db578063a483f4e1146100ee578063f5a31579146100f657600080fd5b8063068ea0eb146100825780630efcb43c1461008c57806342cff738146100a3575b600080fd5b61008a6100fe565b005b6004545b6040519081526020015b60405180910390f35b6100ab6101cb565b60405161009a9190610312565b3360009081526020819052604090205460ff16604051901515815260200161009a565b61008a6100e93660046102f9565b61022d565b600354610090565b600254610090565b3360009081526020819052604090205460ff16156101635760405162461bcd60e51b815260206004820152601b60248201527f596f75204861766520416c7265616479204265656e204164646564000000000060448201526064015b60405180910390fd5b336000818152602081905260408120805460ff191660019081179091558054808201825591527fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf601805473ffffffffffffffffffffffffffffffffffffffff19169091179055565b6060600180548060200260200160405190810160405280929190818152602001828054801561022357602002820191906000526020600020905b81546001600160a01b03168152600190910190602001808311610205575b5050505050905090565b60048110801561023d5750600081115b6102af5760405162461bcd60e51b815260206004820152603960248201527f74686520676976656e206e756d62657220697320696e76616c6964206173207460448201527f6865206e756d626572206973206f7574206f662072616e676500000000000000606482015260840161015a565b80600114156102d057600280549060006102c88361035f565b919050555050565b80600214156102e957600380549060006102c88361035f565b600480549060006102c88361035f565b60006020828403121561030b57600080fd5b5035919050565b6020808252825182820181905260009190848201906040850190845b818110156103535783516001600160a01b03168352928401929184019160010161032e565b50909695505050505050565b600060001982141561038157634e487b7160e01b600052601160045260246000fd5b506001019056fea264697066735822122036c7a6eb905bfb9bbcbf2436975878a32327f0fd4a8648f67281fd07be0ff4b064736f6c63430008070033\",\n"
            + "\t\"opcodes\": \"PUSH1 0x80 PUSH1 0x40 MSTORE CALLVALUE DUP1 ISZERO PUSH2 0x10 JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST POP PUSH1 0x0 PUSH1 0x2 DUP2 SWAP1 SSTORE PUSH1 0x3 DUP2 SWAP1 SSTORE PUSH1 0x4 SSTORE PUSH2 0x3BE DUP1 PUSH2 0x2F PUSH1 0x0 CODECOPY PUSH1 0x0 RETURN INVALID PUSH1 0x80 PUSH1 0x40 MSTORE CALLVALUE DUP1 ISZERO PUSH2 0x10 JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST POP PUSH1 0x4 CALLDATASIZE LT PUSH2 0x7D JUMPI PUSH1 0x0 CALLDATALOAD PUSH1 0xE0 SHR DUP1 PUSH4 0x449B6DB2 GT PUSH2 0x5B JUMPI DUP1 PUSH4 0x449B6DB2 EQ PUSH2 0xB8 JUMPI DUP1 PUSH4 0x464D4A92 EQ PUSH2 0xDB JUMPI DUP1 PUSH4 0xA483F4E1 EQ PUSH2 0xEE JUMPI DUP1 PUSH4 0xF5A31579 EQ PUSH2 0xF6 JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST DUP1 PUSH4 0x68EA0EB EQ PUSH2 0x82 JUMPI DUP1 PUSH4 0xEFCB43C EQ PUSH2 0x8C JUMPI DUP1 PUSH4 0x42CFF738 EQ PUSH2 0xA3 JUMPI JUMPDEST PUSH1 0x0 DUP1 REVERT JUMPDEST PUSH2 0x8A PUSH2 0xFE JUMP JUMPDEST STOP JUMPDEST PUSH1 0x4 SLOAD JUMPDEST PUSH1 0x40 MLOAD SWAP1 DUP2 MSTORE PUSH1 0x20 ADD JUMPDEST PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 RETURN JUMPDEST PUSH2 0xAB PUSH2 0x1CB JUMP JUMPDEST PUSH1 0x40 MLOAD PUSH2 0x9A SWAP2 SWAP1 PUSH2 0x312 JUMP JUMPDEST CALLER PUSH1 0x0 SWAP1 DUP2 MSTORE PUSH1 0x20 DUP2 SWAP1 MSTORE PUSH1 0x40 SWAP1 KECCAK256 SLOAD PUSH1 0xFF AND PUSH1 0x40 MLOAD SWAP1 ISZERO ISZERO DUP2 MSTORE PUSH1 0x20 ADD PUSH2 0x9A JUMP JUMPDEST PUSH2 0x8A PUSH2 0xE9 CALLDATASIZE PUSH1 0x4 PUSH2 0x2F9 JUMP JUMPDEST PUSH2 0x22D JUMP JUMPDEST PUSH1 0x3 SLOAD PUSH2 0x90 JUMP JUMPDEST PUSH1 0x2 SLOAD PUSH2 0x90 JUMP JUMPDEST CALLER PUSH1 0x0 SWAP1 DUP2 MSTORE PUSH1 0x20 DUP2 SWAP1 MSTORE PUSH1 0x40 SWAP1 KECCAK256 SLOAD PUSH1 0xFF AND ISZERO PUSH2 0x163 JUMPI PUSH1 0x40 MLOAD PUSH3 0x461BCD PUSH1 0xE5 SHL DUP2 MSTORE PUSH1 0x20 PUSH1 0x4 DUP3 ADD MSTORE PUSH1 0x1B PUSH1 0x24 DUP3 ADD MSTORE PUSH32 0x596F75204861766520416C7265616479204265656E2041646465640000000000 PUSH1 0x44 DUP3 ADD MSTORE PUSH1 0x64 ADD JUMPDEST PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 REVERT JUMPDEST CALLER PUSH1 0x0 DUP2 DUP2 MSTORE PUSH1 0x20 DUP2 SWAP1 MSTORE PUSH1 0x40 DUP2 KECCAK256 DUP1 SLOAD PUSH1 0xFF NOT AND PUSH1 0x1 SWAP1 DUP2 OR SWAP1 SWAP2 SSTORE DUP1 SLOAD DUP1 DUP3 ADD DUP3 SSTORE SWAP2 MSTORE PUSH32 0xB10E2D527612073B26EECDFD717E6A320CF44B4AFAC2B0732D9FCBE2B7FA0CF6 ADD DUP1 SLOAD PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF NOT AND SWAP1 SWAP2 OR SWAP1 SSTORE JUMP JUMPDEST PUSH1 0x60 PUSH1 0x1 DUP1 SLOAD DUP1 PUSH1 0x20 MUL PUSH1 0x20 ADD PUSH1 0x40 MLOAD SWAP1 DUP2 ADD PUSH1 0x40 MSTORE DUP1 SWAP3 SWAP2 SWAP1 DUP2 DUP2 MSTORE PUSH1 0x20 ADD DUP3 DUP1 SLOAD DUP1 ISZERO PUSH2 0x223 JUMPI PUSH1 0x20 MUL DUP3 ADD SWAP2 SWAP1 PUSH1 0x0 MSTORE PUSH1 0x20 PUSH1 0x0 KECCAK256 SWAP1 JUMPDEST DUP2 SLOAD PUSH1 0x1 PUSH1 0x1 PUSH1 0xA0 SHL SUB AND DUP2 MSTORE PUSH1 0x1 SWAP1 SWAP2 ADD SWAP1 PUSH1 0x20 ADD DUP1 DUP4 GT PUSH2 0x205 JUMPI JUMPDEST POP POP POP POP POP SWAP1 POP SWAP1 JUMP JUMPDEST PUSH1 0x4 DUP2 LT DUP1 ISZERO PUSH2 0x23D JUMPI POP PUSH1 0x0 DUP2 GT JUMPDEST PUSH2 0x2AF JUMPI PUSH1 0x40 MLOAD PUSH3 0x461BCD PUSH1 0xE5 SHL DUP2 MSTORE PUSH1 0x20 PUSH1 0x4 DUP3 ADD MSTORE PUSH1 0x39 PUSH1 0x24 DUP3 ADD MSTORE PUSH32 0x74686520676976656E206E756D62657220697320696E76616C69642061732074 PUSH1 0x44 DUP3 ADD MSTORE PUSH32 0x6865206E756D626572206973206F7574206F662072616E676500000000000000 PUSH1 0x64 DUP3 ADD MSTORE PUSH1 0x84 ADD PUSH2 0x15A JUMP JUMPDEST DUP1 PUSH1 0x1 EQ ISZERO PUSH2 0x2D0 JUMPI PUSH1 0x2 DUP1 SLOAD SWAP1 PUSH1 0x0 PUSH2 0x2C8 DUP4 PUSH2 0x35F JUMP JUMPDEST SWAP2 SWAP1 POP SSTORE POP POP JUMP JUMPDEST DUP1 PUSH1 0x2 EQ ISZERO PUSH2 0x2E9 JUMPI PUSH1 0x3 DUP1 SLOAD SWAP1 PUSH1 0x0 PUSH2 0x2C8 DUP4 PUSH2 0x35F JUMP JUMPDEST PUSH1 0x4 DUP1 SLOAD SWAP1 PUSH1 0x0 PUSH2 0x2C8 DUP4 PUSH2 0x35F JUMP JUMPDEST PUSH1 0x0 PUSH1 0x20 DUP3 DUP5 SUB SLT ISZERO PUSH2 0x30B JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST POP CALLDATALOAD SWAP2 SWAP1 POP JUMP JUMPDEST PUSH1 0x20 DUP1 DUP3 MSTORE DUP3 MLOAD DUP3 DUP3 ADD DUP2 SWAP1 MSTORE PUSH1 0x0 SWAP2 SWAP1 DUP5 DUP3 ADD SWAP1 PUSH1 0x40 DUP6 ADD SWAP1 DUP5 JUMPDEST DUP2 DUP2 LT ISZERO PUSH2 0x353 JUMPI DUP4 MLOAD PUSH1 0x1 PUSH1 0x1 PUSH1 0xA0 SHL SUB AND DUP4 MSTORE SWAP3 DUP5 ADD SWAP3 SWAP2 DUP5 ADD SWAP2 PUSH1 0x1 ADD PUSH2 0x32E JUMP JUMPDEST POP SWAP1 SWAP7 SWAP6 POP POP POP POP POP POP JUMP JUMPDEST PUSH1 0x0 PUSH1 0x0 NOT DUP3 EQ ISZERO PUSH2 0x381 JUMPI PUSH4 0x4E487B71 PUSH1 0xE0 SHL PUSH1 0x0 MSTORE PUSH1 0x11 PUSH1 0x4 MSTORE PUSH1 0x24 PUSH1 0x0 REVERT JUMPDEST POP PUSH1 0x1 ADD SWAP1 JUMP INVALID LOG2 PUSH5 0x6970667358 0x22 SLT KECCAK256 CALLDATASIZE 0xC7 0xA6 0xEB SWAP1 JUMPDEST 0xFB SWAP12 0xBC 0xBF 0x24 CALLDATASIZE SWAP8 PC PUSH25 0xA32327F0FD4A8648F67281FD07BE0FF4B064736F6C63430008 SMOD STOP CALLER \",\n"
            + "\t\"sourceMap\": \"74:1607:0:-:0;;;272:108;;;;;;;;;-1:-1:-1;313:1:0;297:13;:17;;;325:13;:17;;;353:15;:19;74:1607;;;;;;\"\n"
            + "}";

    public static final String FUNC_ADDMETOVOTEDLIST = "addMeToVotedList";

    public static final String FUNC_GETADDRESSVALUES = "getAddressValues";

    public static final String FUNC_GETPARTY1VOTES = "getParty1Votes";

    public static final String FUNC_GETPARTY2VOTES = "getParty2Votes";

    public static final String FUNC_GETPARTY3VOTES = "getParty3Votes";

    public static final String FUNC_HASALREADYVOTED = "hasAlreadyVoted";

    public static final String FUNC_REGISTERVOTE = "registerVote";

    @Deprecated
    protected ContractHex(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ContractHex(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ContractHex(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ContractHex(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    @Deprecated
    public static ContractHex load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ContractHex(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ContractHex load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ContractHex(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ContractHex load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ContractHex(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ContractHex load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ContractHex(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<ContractHex> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ContractHex.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<ContractHex> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(ContractHex.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ContractHex> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ContractHex.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<ContractHex> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(ContractHex.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public RemoteFunctionCall<TransactionReceipt> addMeToVotedList() {
        final Function function = new Function(
                FUNC_ADDMETOVOTEDLIST,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<List> getAddressValues() {
        final Function function = new Function(FUNC_GETADDRESSVALUES,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {
                }));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> getParty1Votes() {
        final Function function = new Function(FUNC_GETPARTY1VOTES,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getParty2Votes() {
        final Function function = new Function(FUNC_GETPARTY2VOTES,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getParty3Votes() {
        final Function function = new Function(FUNC_GETPARTY3VOTES,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> hasAlreadyVoted() {
        final Function function = new Function(FUNC_HASALREADYVOTED,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
                }));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerVote(BigInteger num) {
        final Function function = new Function(
                FUNC_REGISTERVOTE,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(num)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }
}
