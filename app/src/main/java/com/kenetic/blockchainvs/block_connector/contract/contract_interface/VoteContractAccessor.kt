package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.util.Log
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.*
import org.web3j.abi.datatypes.Array
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Contract
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.utils.Convert
import java.math.BigInteger


private const val TAG = "VoteContractAccessor"

// TODO: check values for binary and addresses
private const val CONTRACT_BINARY =
    "608060405234801561001057600080fd5b506000600181905550600060028190555060006003819055506106ea806100386000396000f3fe608060405234801561001057600080fd5b50600436106100625760003560e01c80630efcb43c1461006757806342cff73814610085578063449b6db2146100a3578063464d4a92146100c1578063a483f4e1146100dd578063f5a31579146100fb575b600080fd5b61006f610119565b60405161007c91906104f5565b60405180910390f35b61008d610123565b60405161009a9190610498565b60405180910390f35b6100ab6101b1565b6040516100b891906104ba565b60405180910390f35b6100db60048036038101906100d691906103a5565b610261565b005b6100e561037c565b6040516100f291906104f5565b60405180910390f35b610103610386565b60405161011091906104f5565b60405180910390f35b6000600354905090565b606060008054806020026020016040519081016040528092919081815260200182805480156101a757602002820191906000526020600020905b8160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001906001019080831161015d575b5050505050905090565b60008033905060005b60008054905081101561025757600081815481106101db576101da61061a565b5b9060005260206000200160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156102445760019250505061025e565b808061024f906105a2565b9150506101ba565b6000925050505b90565b6004811080156102715750600081115b6102b0576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016102a7906104d5565b60405180910390fd5b6000339080600181540180825580915050600190039060005260206000200160009091909190916101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506001811415610339576001600081548092919061032f906105a2565b9190505550610379565b600281141561035f5760026000815480929190610355906105a2565b9190505550610378565b60036000815480929190610372906105a2565b91905055505b5b50565b6000600254905090565b6000600154905090565b60008135905061039f8161069d565b92915050565b6000602082840312156103bb576103ba610649565b5b60006103c984828501610390565b91505092915050565b60006103de83836103ea565b60208301905092915050565b6103f38161055a565b82525050565b600061040482610520565b61040e8185610538565b935061041983610510565b8060005b8381101561044a57815161043188826103d2565b975061043c8361052b565b92505060018101905061041d565b5085935050505092915050565b6104608161056c565b82525050565b6000610473603983610549565b915061047e8261064e565b604082019050919050565b61049281610598565b82525050565b600060208201905081810360008301526104b281846103f9565b905092915050565b60006020820190506104cf6000830184610457565b92915050565b600060208201905081810360008301526104ee81610466565b9050919050565b600060208201905061050a6000830184610489565b92915050565b6000819050602082019050919050565b600081519050919050565b6000602082019050919050565b600082825260208201905092915050565b600082825260208201905092915050565b600061056582610578565b9050919050565b60008115159050919050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b6000819050919050565b60006105ad82610598565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8214156105e0576105df6105eb565b5b600182019050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b7f4e487b7100000000000000000000000000000000000000000000000000000000600052603260045260246000fd5b600080fd5b7f74686520676976656e206e756d62657220697320696e76616c6964206173207460008201527f6865206e756d626572206973206f7574206f662072616e676500000000000000602082015250565b6106a681610598565b81146106b157600080fd5b5056fea26469706673582212205da625bda0850f1ae09940b191c98f12fd976f993abe0e3ed1fa777c406ebf0564736f6c63430008070033"
private const val CONTRACT_ADDRESS = "0xe2d8a60415adaa2Ba87c44b8C20C9A15e3F9178a"
private const val USER_ACCOUNT = "0xE4e609e2E928E8F8b74C6Bb37e13503b337f8C70"
private val USER_PRIVATE_KEY =
    "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"

class VoteContractAccessor(
    web3j: Web3j, transactionManager: TransactionManager, gasProvider: ContractGasProvider
) :
    Contract(
        CONTRACT_BINARY, CONTRACT_ADDRESS, web3j, transactionManager, gasProvider
    ) {
    //
    private val functionGetHasAlreadyVoted = "hasAlreadyVoted"

    //----------------------------------------------------------------------------------casting-vote
    private val functionRegisterVote = "registerVote"

    //--------------------------------------------------------------------------------view-functions
    private val functionGetAddressValues = "getAddressValues"
    private val functionGetPartyOneVotes = "getParty1Votes"
    private val functionGetPartyTwoVotes = "getParty2Votes"
    private val functionGetPartyThreeVotes = "getParty3Votes"

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

    // TODO: make changes to parameters
    fun putVote(party: PartyEnum): RemoteCall<TransactionReceipt> {
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
        return executeRemoteCallTransaction(function)
    }

    // TODO: ask stackoverflow
    fun test1() {
        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(Uint256(3)),
            emptyList()
        )
        val txData = FunctionEncoder.encode(function)
        val txManager = RawTransactionManager(web3j, Credentials.create(USER_ACCOUNT))
        //Error thrown at below line
        val txHash = txManager.sendTransaction(
            this.requestCurrentGasPrice(), DefaultGasProvider.GAS_LIMIT, CONTRACT_ADDRESS, txData,
            BigInteger.ZERO
        ).transactionHash
        val receiptProcessor = PollingTransactionReceiptProcessor(
            web3j, TransactionManager.DEFAULT_POLLING_FREQUENCY,
            TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH
        )
        val txReceipt = receiptProcessor.waitForTransactionReceipt(txHash)
        Log.d(TAG, "transaction receipt = \ngas used = ${txReceipt.gasUsed}")
    }

    //--------------------------------------------------------------------does-not-require-ether
    fun test2(party: PartyEnum) {
        val contactAddress = "0xe2d8a60415adaa2Ba87c44b8C20C9A15e3F9178a"
        val userAccount = "0xE4e609e2E928E8F8b74C6Bb37e13503b337f8C70"
        val userPrivateKey = "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
        val hostUrl = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"
        val web3Obj = Web3j.build(HttpService(hostUrl))
        val projectSecret = "c9852fcf061b47c58d5294cd7a23548c"
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
        val encodedFunction = FunctionEncoder.encode(function)
        val response = web3Obj
            .ethCall(
                Transaction.createEthCallTransaction(userAccount, contactAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
            ).sendAsync().get()
    }


    fun test4() {
        val functionRegisterVote = "registerVote"
        val contactAddress = "0xe2d8a60415adaa2Ba87c44b8C20C9A15e3F9178a"
        val userAccount = "0xE4e609e2E928E8F8b74C6Bb37e13503b337f8C70"
        val userPrivateKey = "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
        val hostUrl = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"
        val web3Obj = Web3j.build(HttpService(hostUrl))
        val credentials = Credentials.create(userPrivateKey, userAccount)

        val function = Function(
            functionRegisterVote,
            listOf<Type<*>>(
                Uint256(2)
            ),
            emptyList()
        )
        /**
         * the below log statements generates the balance as zero even though
         * the account has 0.99 eth on the test net. The contract was deployed using
         * the testnet ether
         */
        val statement1 = Convert.fromWei(
            web3Obj
                .ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST)
                .send().balance.toString(),
            Convert.Unit.ETHER
        )
        // log statements are just print statements. TAG = "VoteContractAccessor"
        // and the rest is the message
        Log.d(TAG, "user balance = $statement1")
        /**
         * Error processing transaction request: exceeds block gas limit
         */
        val transactionReceipt = executeRemoteCallTransaction(function).sendAsync().get()
        Log.d(TAG, "transaction receipt, gas used = ${transactionReceipt.gasUsed}")
    }

    fun test3(party: PartyEnum) {
        val contactAddress = "0xe2d8a60415adaa2Ba87c44b8C20C9A15e3F9178a"
        val userAccount = "0xE4e609e2E928E8F8b74C6Bb37e13503b337f8C70"
        val userPrivateKey = "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
        val hostUrl = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"
        val web3Obj = Web3j.build(HttpService(hostUrl))
        val credentials = Credentials.create(userPrivateKey, userAccount)
        val projectSecret = "c9852fcf061b47c58d5294cd7a23548c"

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
        /**
         * the below log statements generates the balance as zero even though
         * the account has 0.99 eth on the test net. The contract was deployed using
         * the testnet ether
         */
        Log.d(
            TAG, "user balance = ${
                Convert.fromWei(
                    web3Obj
                        .ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST)
                        .send().balance.toString(),
                    Convert.Unit.ETHER
                )
            }"
        )
        val transactionReceipt = executeRemoteCallTransaction(function).sendAsync().get()
        /**
         * Error processing transaction request: exceeds block gas limit
         */
        Log.d(TAG, "transaction receipt, gas used = ${transactionReceipt.gasUsed}")
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