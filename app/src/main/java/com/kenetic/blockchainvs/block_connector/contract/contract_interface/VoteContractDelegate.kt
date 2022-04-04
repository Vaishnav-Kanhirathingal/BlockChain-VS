package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.annotation.SuppressLint
import android.util.Log
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.datapack.database.TransactionData
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ChainIdLong
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "VoteContractDelegate"

class VoteContractDelegate(private val viewModel: MainViewModel) {
    //------------------------------------------------------------------------------project-elements
    private val CONTRACT_ADDRESS = "0x84D46ba7aAac6221DF9038d3Ccf41F1cd46001aF"

    //-----------------------------------------------------------------------------------credentials
    private val USER_PRIVATE_KEY =
        "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
    private val credentials = Credentials.create(USER_PRIVATE_KEY)

    private val ROPSTEN_INFURA_URL = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"
    private val web3j: Web3j = Web3j.build(HttpService(ROPSTEN_INFURA_URL))

    //--------------------------------------------------------------------------------gas-controller
    private val gasPrice: BigInteger = Convert.toWei("200", Convert.Unit.GWEI).toBigInteger()
    private val gasLimit = BigInteger.valueOf(40000)
    private val gasProvider: ContractGasProvider = StaticGasProvider(gasPrice, gasLimit)

    private var contract: ContractHex = ContractHex.load(
        CONTRACT_ADDRESS,
        web3j,
        RawTransactionManager(web3j, credentials, ChainIdLong.ROPSTEN),
        gasProvider
    )

    @SuppressLint("SimpleDateFormat")
    fun registerVote(party: PartyEnum): String {
        val nonce = web3j
            .ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
            .send()
            .transactionCount

        return try {
            Log.d(TAG, "nonce for transaction nonce = $nonce")

            val rawTransaction = RawTransaction.createTransaction(
                ChainIdLong.ROPSTEN,
                nonce,
                gasLimit,
                CONTRACT_ADDRESS,
                BigInteger.ZERO,
                AlternateTransactionHandler().registerVoteEncoded(
                    when (party) {
                        PartyEnum.ONE -> 1
                        PartyEnum.TWO -> 2
                        PartyEnum.THREE -> 3
                    }
                ),
                gasPrice,
                gasPrice
            )

            val transactionHash = getTransactionHash(rawTransaction)
            val txData = TransactionData(
                transactionHash = transactionHash,
                methodCalled = "registerVote",
                gasFee = null,
                transactionTime = SimpleDateFormat("HH:mm:ss - dd/MM/yy").format(
                    Date(System.currentTimeMillis())
                )
            )
            viewModel.insertParty(txData)
            Log.d(TAG, "transactionHash = $transactionHash, generating receipt...")

            val transactionReceipt = generateReceipt(transactionHash)

            Log.d(TAG,"transaction re")
            txData.gasFee = transactionReceipt.gasUsed.toLong()
            viewModel.updateParty(txData)

            Log.d(TAG, "transactionReceipt = $transactionReceipt")
            transactionReceipt.gasUsed.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            e.message.toString()
        }
    }

    fun addMeToVotedList(): String {
        val nonce = web3j
            .ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
            .send()
            .transactionCount

        return try {
            Log.d(TAG, "nonce for transaction nonce = $nonce")

            val rawTransaction = RawTransaction.createTransaction(
                ChainIdLong.ROPSTEN,
                nonce,
                gasLimit,
                CONTRACT_ADDRESS,
                BigInteger.ZERO,
                AlternateTransactionHandler().addMeToVotedListEncoded(),
                gasPrice,
                gasPrice
            )

            val transactionHash = getTransactionHash(rawTransaction)
            val txData = TransactionData(
                transactionHash = transactionHash,
                methodCalled = "addMeToVotedList",
                gasFee = null,
                transactionTime = SimpleDateFormat("HH:mm:ss - dd/MM/yy").format(
                    Date(System.currentTimeMillis())
                )
            )
            viewModel.insertParty(txData)
            Log.d(TAG, "transactionHash = $transactionHash, generating receipt...")

            val transactionReceipt = generateReceipt(transactionHash)

            txData.gasFee = transactionReceipt.gasUsed.toLong()
            viewModel.updateParty(txData)

            Log.d(TAG, "transactionReceipt = $transactionReceipt")

            transactionReceipt.gasUsed.toString()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            e.message.toString()
        }
    }

    private fun generateReceipt(txHash: String): TransactionReceipt {
        do {
            Thread.sleep(5000)
            Log.d(TAG, "trying to get transaction receipt")
        } while (!web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.isPresent)
        return web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()
    }

    private fun getTransactionHash(rawTransaction: RawTransaction): String {
        return web3j.ethSendRawTransaction(
            Numeric.toHexString(
                TransactionEncoder.signMessage(rawTransaction, ChainIdLong.ROPSTEN, credentials)
            )
        ).send().transactionHash
    }

    //------------------------------------------------------------------------------------------done
    fun partyVotesStatus(): String {
        return try {
            val votesForOne: Int = contract.party1Votes.send().toInt()
            val votesForTwo: Int = contract.party2Votes.send().toInt()
            val votesForThree: Int = contract.party3Votes.send().toInt()
            "party 1 votes = " + votesForOne +
                    "\nparty 2 votes = " + votesForTwo +
                    "\nparty 3 votes = " + votesForThree
        } catch (e: Exception) {
            e.printStackTrace()
            "error has occurred, ${e.message}"
        }
    }

    fun getVoterAddresses(): String {
        return try {
            val x = contract.addressValues.send().toString()
            "voters:-\n$x"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error - ${e.message}"
        }
    }

    fun getHasAlreadyVoted(): String {
        return try {
            contract.hasAlreadyVoted().send().toString()
        } catch (e: Exception) {
            "Error has occurred while making calls :-\n${e.message}"
        }
    }

    fun getBalance(): BigDecimal {
        return try {
            Convert.fromWei(
                web3j.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST)
                    .send().balance.toString(),
                Convert.Unit.ETHER
            )
        } catch (e: Exception) {
            BigDecimal(0)
        }
    }
}