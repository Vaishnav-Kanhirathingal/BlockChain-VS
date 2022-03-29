package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import org.web3j.protocol.core.methods.response.EthGetTransactionCount
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.tx.ChainIdLong
import org.web3j.tx.Contract
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*


private const val TAG = "VoteContractAccessor"
private const val CONTRACT_BINARY =
    "608060405234801561001057600080fd5b506000600281905560038190556004556103be8061002f6000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c8063449b6db21161005b578063449b6db2146100b8578063464d4a92146100db578063a483f4e1146100ee578063f5a31579146100f657600080fd5b8063068ea0eb146100825780630efcb43c1461008c57806342cff738146100a3575b600080fd5b61008a6100fe565b005b6004545b6040519081526020015b60405180910390f35b6100ab6101cb565b60405161009a9190610312565b3360009081526020819052604090205460ff16604051901515815260200161009a565b61008a6100e93660046102f9565b61022d565b600354610090565b600254610090565b3360009081526020819052604090205460ff16156101635760405162461bcd60e51b815260206004820152601b60248201527f596f75204861766520416c7265616479204265656e204164646564000000000060448201526064015b60405180910390fd5b336000818152602081905260408120805460ff191660019081179091558054808201825591527fb10e2d527612073b26eecdfd717e6a320cf44b4afac2b0732d9fcbe2b7fa0cf601805473ffffffffffffffffffffffffffffffffffffffff19169091179055565b6060600180548060200260200160405190810160405280929190818152602001828054801561022357602002820191906000526020600020905b81546001600160a01b03168152600190910190602001808311610205575b5050505050905090565b60048110801561023d5750600081115b6102af5760405162461bcd60e51b815260206004820152603960248201527f74686520676976656e206e756d62657220697320696e76616c6964206173207460448201527f6865206e756d626572206973206f7574206f662072616e676500000000000000606482015260840161015a565b80600114156102d057600280549060006102c88361035f565b919050555050565b80600214156102e957600380549060006102c88361035f565b600480549060006102c88361035f565b60006020828403121561030b57600080fd5b5035919050565b6020808252825182820181905260009190848201906040850190845b818110156103535783516001600160a01b03168352928401929184019160010161032e565b50909695505050505050565b600060001982141561038157634e487b7160e01b600052601160045260246000fd5b506001019056fea264697066735822122036c7a6eb905bfb9bbcbf2436975878a32327f0fd4a8648f67281fd07be0ff4b064736f6c63430008070033"
private const val CONTRACT_ADDRESS = "0x84D46ba7aAac6221DF9038d3Ccf41F1cd46001aF"

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

    fun test1() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(Uint256(2)),
            emptyList()
        )
        val txData = FunctionEncoder.encode(function)

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
        val receiptProcessor =
            PollingTransactionReceiptProcessor(
                web3obj,
                TransactionManager.DEFAULT_POLLING_FREQUENCY,
                TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH
            )
        val txReceipt = receiptProcessor.waitForTransactionReceipt(txHash)
        Log.d(TAG, "transaction receipt = \ngas used = ${txReceipt.gasUsed}")
    }

    //ethSendTransaction = null
    fun test2() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(2)
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)
        val ethGetTransactionCount =
            web3j
                .ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get()

        val nonce = ethGetTransactionCount.transactionCount
        val rawTransaction = RawTransaction
            .createTransaction(
                nonce,
                requestCurrentGasPrice(),
                DefaultGasProvider.GAS_LIMIT,
                CONTRACT_ADDRESS,
                encodedFunction
            )
        val signedTransaction =
            TransactionEncoder.signMessage(rawTransaction, ChainIdLong.ROPSTEN, credentials)

        val ethSendTransaction =
            web3obj.ethSendRawTransaction(signedTransaction.toString()).send()
        Log.d(TAG, "ethSendTransaction = ${ethSendTransaction.transactionHash}")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun test3() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(2)
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)
        val ethGetTransactionCount: EthGetTransactionCount =
            web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
                .send()

        val nonce = ethGetTransactionCount.transactionCount
        Log.d(TAG, "nonce = $nonce")

        val amountToBeSent: BigDecimal = BigDecimal.valueOf(4000000)
        val cost: BigInteger = Convert.toWei(amountToBeSent, Convert.Unit.ETHER).toBigInteger()
        Log.d(TAG, "cost = $cost")

        val gasLimit = BigInteger.valueOf(4000000)
        val gasPrice = BigInteger.valueOf(4000000)

        val transaction = RawTransaction.createTransaction(
            ChainIdLong.ROPSTEN,
            nonce,
            gasLimit,
            CONTRACT_ADDRESS,
            cost,
            encodedFunction,
            BigInteger.valueOf(4000000),
            BigInteger.valueOf(4000000)
        )

        val signedMessage =
            TransactionEncoder.signMessage(transaction, ChainIdLong.ROPSTEN, credentials)

        val hexValue = Numeric.toHexString(signedMessage)
        Log.d(TAG, "hexValue = $hexValue")

        val gasPrice1 = Convert.toWei("1", Convert.Unit.GWEI).toBigInteger()
        Log.d(TAG,"generated gas price = $gasPrice1, the saved gasPrice = $gasPrice")

        val sentTransaction = web3j.ethSendRawTransaction(hexValue).send()
        val transactionHash = sentTransaction.transactionHash
        Log.d(TAG, "transactionHash = $transactionHash")

        var receipt: Optional<TransactionReceipt>
        do {
            Log.d(TAG, "checking if transactionHash [$transactionHash] is mined yet")
            receipt = web3j.ethGetTransactionReceipt(transactionHash).send().transactionReceipt
            Thread.sleep(3000)
        } while (receipt.isPresent)
        Log.d(TAG, "transaction was mined at [${receipt.get().blockNumber}]")
        Log.d(TAG, "balance = ${printBalance()}")
    }


    fun printBalance(): BigDecimal {
        val balance =
            Convert.fromWei(
                web3obj.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST)
                    .send().balance.toString(),
                Convert.Unit.ETHER
            )

        Log.d(TAG, "user balance = $balance")
        return balance
    }

    // TODO: fix error - late init property has not been initiated
    private lateinit var a: Array<Address>
    fun getAddressValues(): RemoteCall<Array<Address>> {
        val function = Function(
            functionGetAddressValues,
            listOf(),
            listOf<TypeReference<*>>(object : TypeReference<Array<Address>>() {})
        )
        return executeRemoteCallSingleValueReturn(function, a.javaClass)
    }

    fun putVote(party: PartyEnum): RemoteCall<TransactionReceipt> {
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