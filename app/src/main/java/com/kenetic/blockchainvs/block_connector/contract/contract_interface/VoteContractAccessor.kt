package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.util.Log
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Array
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.ChainIdLong
import org.web3j.tx.Contract
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger


private const val TAG = "VoteContractAccessor"
private const val CONTRACT_BINARY =
    "608060405234801561001057600080fd5b506000600181905550600060028190555060006003819055506106ea806100386000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80630efcb43c1461006757806342cff73814610085578063449b6db2146100a3578063464d4a92146100c1578063a483f4e1146100dd578063f5a31579146100fb575b600080fd5b61006f610119565b60405161007c91906104f5565b60405180910390f35b61008d610123565b60405161009a9190610498565b60405180910390f35b6100ab6101b1565b6040516100b891906104ba565b60405180910390f35b6100db60048036038101906100d691906103a5565b610261565b005b6100e561037c565b6040516100f291906104f5565b60405180910390f35b610103610386565b60405161011091906104f5565b60405180910390f35b6000600354905090565b606060008054806020026020016040519081016040528092919081815260200182805480156101a757602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001906001019080831161015d575b5050505050905090565b60008033905060005b60008054905081101561025757600081815481106101db576101da61061a565b5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156102445760019250505061025e565b808061024f906105a2565b9150506101ba565b6000925050505b90565b6004811080156102715750600081115b6102b0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102a7906104d5565b60405180910390fd5b6000339080600181540180825580915050600190039060005260206000200160009091909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001811415610339576001600081548092919061032f906105a2565b9190505550610379565b600281141561035f5760026000815480929190610355906105a2565b9190505550610378565b60036000815480929190610372906105a2565b91905055505b5b50565b6000600254905090565b6000600154905090565b60008135905061039f8161069d565b92915050565b6000602082840312156103bb576103ba610649565b5b60006103c984828501610390565b91505092915050565b60006103de83836103ea565b60208301905092915050565b6103f38161055a565b82525050565b600061040482610520565b61040e8185610538565b935061041983610510565b8060005b8381101561044a57815161043188826103d2565b975061043c8361052b565b92505060018101905061041d565b5085935050505092915050565b6104608161056c565b82525050565b6000610473603983610549565b915061047e8261064e565b604082019050919050565b61049281610598565b82525050565b600060208201905081810360008301526104b281846103f9565b905092915050565b60006020820190506104cf6000830184610457565b92915050565b600060208201905081810360008301526104ee81610466565b9050919050565b600060208201905061050a6000830184610489565b92915050565b6000819050602082019050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600061056582610578565b9050919050565b60008115159050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b60006105ad82610598565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8214156105e0576105df6105eb565b5b600182019050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b600080fd5b7f74686520676976656e206e756d62657220697320696e76616c6964206173207460008201527f6865206e756d626572206973206f7574206f662072616e676500000000000000602082015250565b6106a681610598565b81146106b157600080fd5b5056fea26469706673582212205da625bda0850f1ae09940b191c98f12fd976f993abe0e3ed1fa777c406ebf0564736f6c63430008070033"
private const val CONTRACT_ADDRESS = "0xe2d8a60415adaa2Ba87c44b8C20C9A15e3F9178a"

class VoteContractAccessor(
    private val web3obj: Web3j,
    transactionManager: TransactionManager,
    gasProvider: ContractGasProvider,
    private val credentials: Credentials
) :
    Contract(
        CONTRACT_BINARY,
        CONTRACT_ADDRESS,
        web3obj,
        transactionManager,
        gasProvider
    ) {
    //--------------------------------------------------------------------------------function-names
    private val functionGetHasAlreadyVoted = "hasAlreadyVoted"
    private val functionRegisterVote = "registerVote"
    private val functionGetAddressValues = "getAddressValues"
    private val functionGetPartyOneVotes = "getParty1Votes"
    private val functionGetPartyTwoVotes = "getParty2Votes"
    private val functionGetPartyThreeVotes = "getParty3Votes"

    init {
        gasProvider.getGasPrice()
    }

    //complete-fail
    fun test1() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(2)
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)
        val response =
            web3obj.ethCall(
                Transaction
                    .createEthCallTransaction(
                        credentials.address,
                        CONTRACT_ADDRESS,
                        encodedFunction
                    ), DefaultBlockParameterName.LATEST
            ).sendAsync().get()
        Log.d(TAG, "response.value = ${response.value}")
    }

    //-----------------------------------------------------------------------------------------check
    /**
     * Error processing transaction request:
     * exceeds block gas limit
     */
    fun test2() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(2)
            ),
            emptyList()
        )
        val transactionReceipt = executeRemoteCallTransaction(function).sendAsync().get()
        Log.d(TAG, "transaction receipt, gas used = ${transactionReceipt.gasUsed}")
    }

    /**
     * Error processing request: invalid argument 0: json:
     * cannot unmarshal non-string into Go value of type common.Hash
     */
    fun test3() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(2)
            ),
            emptyList()
        )
        val txData = FunctionEncoder.encode(function)
        // TODO: use transaction manager 'raw' with chain-id = 3
        val txManager = RawTransactionManager(
            web3obj, credentials, ChainIdLong.ROPSTEN
        )
        Log.d(TAG, "web3Obj.netVersion().send().id = ${web3obj.netVersion().send().netVersion}")

        val txHash = txManager
            .sendTransaction(
                this.requestCurrentGasPrice(),
                DefaultGasProvider.GAS_LIMIT,
                CONTRACT_ADDRESS,
                txData,
                BigInteger.ZERO
            ).transactionHash
        val receiptProcessor = PollingTransactionReceiptProcessor(
            web3obj, TransactionManager.DEFAULT_POLLING_FREQUENCY,
            TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH
        )
        val txReceipt = receiptProcessor.waitForTransactionReceipt(txHash)
        Log.d(TAG, "transaction receipt = \ngas used = ${txReceipt.gasUsed}")
    }

    //ethSendTransaction = null
    fun test4() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(2)
            ),
            emptyList()
        )
        val txData = FunctionEncoder.encode(function)
        val ethGetTransactionCount =
            web3j
                .ethGetTransactionCount(CONTRACT_ADDRESS, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get()

        val nonce = ethGetTransactionCount.transactionCount
        val rawTransaction = RawTransaction
            .createTransaction(
                nonce,
                requestCurrentGasPrice(),
                DefaultGasProvider.GAS_LIMIT,
                CONTRACT_ADDRESS,
                txData
            )
        val signedTransaction =
            TransactionEncoder.signMessage(rawTransaction, ChainIdLong.ROPSTEN, credentials)

        val ethSendTransaction =
            web3obj.ethSendRawTransaction(signedTransaction.toString()).send()
        Log.d(TAG, "ethSendTransaction = ${ethSendTransaction.transactionHash}")
    }

    fun test5() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(2)
            ),
            emptyList()
        )
        val txData = FunctionEncoder.encode(function)
        val x =
            web3obj.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
                .send()
        val y = web3obj.ethEstimateGas(
            Transaction.createEthCallTransaction(
                credentials.address,
                CONTRACT_ADDRESS, txData
            )
        ).sendAsync().get()
        Log.d(TAG, "y = ${y.rawResponse}")
    }

    fun printBalance(): BigDecimal {
        val balance =
            Convert.fromWei(
                web3obj.ethGetBalance(
                    credentials.address, DefaultBlockParameterName.LATEST
                )
                    .send().balance.toString(),
                Convert.Unit.ETHER
            )

        Log.d(TAG, "user balance = $balance")
        return balance
    }

    // TODO: fix error - late init property has not been initiated
    lateinit var a: Array<Address>
    fun getAddressValues(): RemoteCall<Array<Address>> {
        val function = Function(
            functionGetAddressValues,
            listOf(),
            listOf<TypeReference<*>>(object : TypeReference<Array<Address>>() {})
        )
        return executeRemoteCallSingleValueReturn(function, a.javaClass)
    }

    fun putVote(party: PartyEnum): RemoteCall<TransactionReceipt> {
        val x = web3j.ethBlockNumber().send()
        Log.d(TAG, "gas price = ${x.rawResponse}")
        Log.d(TAG, "putVote\n\n\nstarted")
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(
                    when (party) {
                        PartyEnum.ONE -> 1
                        PartyEnum.TWO -> 2
                        PartyEnum.THREE -> 3
                    }
                )
            ),
            emptyList()
        )

        Log.d(TAG, "gas price = ${gasProvider.getGasPrice(functionRegisterVote)}")
        return executeRemoteCallTransaction(function)
    }

    //------------------------------------------------------------------------------------------done
    fun getPartyVotes(party: PartyEnum): RemoteCall<Uint256> {
        val function = Function(
            when (party) {
                PartyEnum.ONE -> {
                    functionGetPartyOneVotes
                }
                PartyEnum.TWO -> {
                    functionGetPartyTwoVotes
                }
                PartyEnum.THREE -> {
                    functionGetPartyThreeVotes
                }
            },
            listOf(),
            listOf<TypeReference<*>>(object : TypeReference<Uint256>() {})
        )
        return executeRemoteCallSingleValueReturn(function, Uint256::class.java)
    }

    //------------------------------------------------------------------------------------------done
    fun getHasAlreadyVoted(): RemoteCall<Bool> {
        val function = Function(
            functionGetHasAlreadyVoted,
            listOf(),
            listOf<TypeReference<*>>(object : TypeReference<Bool>() {})
        )
        return executeRemoteCallSingleValueReturn(function, Bool::class.java)
    }
}