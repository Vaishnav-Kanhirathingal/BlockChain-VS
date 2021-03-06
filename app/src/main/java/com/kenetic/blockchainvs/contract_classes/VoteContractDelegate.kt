package com.kenetic.blockchainvs.contract_classes

import android.util.Log
import com.kenetic.blockchainvs.app_viewmodel.MainViewModel
import com.kenetic.blockchainvs.contract_classes.contract_auto_generate.ContractAutoGenOriginal
import com.kenetic.blockchainvs.contract_classes.contract_auto_generate.ContractAutoGenTesting
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
import java.math.BigInteger

private const val TAG = "VoteContractDelegate"

class VoteContractDelegate(private val viewModel: MainViewModel) {
    //------------------------------------------------------------------------------project-elements
    private val ORIGINAL_CONTRACT_ADDRESS = "0xeaef36118C136cE9a19f3Ad99Cb8a9E3Ba634f63"
    private val TESTING_CONTRACT_ADDRESS = "0xFe21E30B76dB57ec7329eA6d0594b258034356A8"

    //-----------------------------------------------------------------------------------credentials
    private val USER_PRIVATE_KEY: String = viewModel.accountPrivateKey
    private val credentials: Credentials = Credentials.create(USER_PRIVATE_KEY)

    private val ROPSTEN_INFURA_URL = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"
    private val web3j: Web3j = Web3j.build(HttpService(ROPSTEN_INFURA_URL))

    //--------------------------------------------------------------------------------gas-controller
    val gweiPrice = "60"
    private val gasPrice: BigInteger = Convert.toWei(gweiPrice, Convert.Unit.GWEI).toBigInteger()
    private val gasLimit = BigInteger.valueOf(120000)
    private val maxPriorityFeePerGas = Convert.toWei("4", Convert.Unit.GWEI).toBigInteger()
    private val maxFeePerGas = Convert.toWei("100", Convert.Unit.GWEI).toBigInteger()

    private val gasProvider: ContractGasProvider = StaticGasProvider(gasPrice, gasLimit)

    //-------------------------------------------------------------------------------testing-setters
    private val originalContract = ContractAutoGenOriginal.load(
        ORIGINAL_CONTRACT_ADDRESS,
        web3j,
        RawTransactionManager(web3j, credentials, ChainIdLong.ROPSTEN),
        gasProvider
    )

    private var testingContract: ContractAutoGenTesting = ContractAutoGenTesting.load(
        TESTING_CONTRACT_ADDRESS,
        web3j,
        RawTransactionManager(web3j, credentials, ChainIdLong.ROPSTEN),
        gasProvider
    )

    private var contract = testingContract

    fun registerVote(party: PartyEnum): TransactionReceipt? {
        return try {
            val rawTransaction = getRawTransaction(
                AlternateTransactionHandler().registerVoteEncoded(
                    when (party) {
                        PartyEnum.ONE -> 1
                        PartyEnum.TWO -> 2
                        PartyEnum.THREE -> 3
                    }
                )
            )

            val transactionHash = getTransactionHash(rawTransaction)
            val txData = TransactionData(
                transactionHash = transactionHash,
                methodCalled = "registerVote",
                gasFee = null,
                transactionTime = System.currentTimeMillis(),
                transactionSuccessful = null
            )
            viewModel.insertTransaction(txData)
            Log.d(TAG, "transactionHash = $transactionHash, generating receipt...")
            generateReceipt(transactionHash)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    fun addMeToVotedList(): TransactionReceipt? {
        return try {
            val rawTransaction = getRawTransaction(
                AlternateTransactionHandler().addMeToVotedListEncoded()
            )

            val transactionHash = getTransactionHash(rawTransaction)
            val txData = TransactionData(
                transactionHash = transactionHash,
                methodCalled = "addMeToVotedList",
                gasFee = null,
                transactionTime = System.currentTimeMillis(),
                transactionSuccessful = null
            )
            viewModel.insertTransaction(txData)
            Log.d(TAG, "transactionHash = $transactionHash, generating receipt...")
            generateReceipt(transactionHash)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getRawTransaction(data: String): RawTransaction {
        val nonce = web3j
            .ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
            .send()
            .transactionCount
        Log.d(TAG, "nonce for transaction nonce = $nonce")
        return RawTransaction.createTransaction(
            ChainIdLong.ROPSTEN,
            nonce,
            gasLimit,
            TESTING_CONTRACT_ADDRESS,
            BigInteger.ZERO,
            data,
            maxPriorityFeePerGas,
            maxFeePerGas
        )
    }

    private fun generateReceipt(txHash: String): TransactionReceipt {
        do {
            Thread.sleep(5000)
            Log.d(TAG, "trying to get transaction receipt")
        } while (!web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.isPresent)
        val receipt = web3j.ethGetTransactionReceipt(txHash).send().transactionReceipt.get()
        Log.d(TAG, "receipt = ${receipt.toString()}")
        return receipt
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
            val addressArray = contract.addressValues.send().toList()
            var addressString = ""
            for (i in addressArray) {
                addressString += "- ${i.toString()}\n"
            }
            Log.d(TAG, "address string = $addressString")
            addressString.dropLast(1)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error - ${e.message}"
        }
    }

    fun getHasAlreadyVoted(): String {
        return try {
            if (contract.hasAlreadyVoted().send()) {
                "You Have Already Voted"
            } else {
                "Your Vote Has Not Been Registered Yet"
            }
        } catch (e: Exception) {
            "Error has occurred while making calls :-\n${e.message}"
        }
    }

    fun getBalance(): String {
        return try {
            Convert.fromWei(
                web3j.ethGetBalance(credentials.address, DefaultBlockParameterName.LATEST)
                    .send().balance.toString(),
                Convert.Unit.ETHER
            ).toString() + "ETH"
        } catch (e: Exception) {
            e.printStackTrace()
            e.message.toString()
        }
    }
}