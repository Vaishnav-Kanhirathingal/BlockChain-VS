package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ChainIdLong
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit

private const val TAG = "VoteContractDelegate"

class VoteContractDelegate() {
    //-----------------------------------------------------------------------------contract-elements
    // TODO: get this from data store
    private val USER_PRIVATE_KEY =
        "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
    private val ROPSTEN_INFURA_URL = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"

    private val credentials = Credentials.create(USER_PRIVATE_KEY)

    private lateinit var web3j: Web3j//--------------------------------------------works-as-intended
    private lateinit var contract: VoteContractAccessor

    // TODO: set correct value
    private val gasPrice: BigInteger = Convert.toWei("40", Convert.Unit.GWEI).toBigInteger()

    //this gas limit value is from deployment and has to be constant
    private val gasLimit = BigInteger.valueOf(4000000)

    private val gasProvider: ContractGasProvider = StaticGasProvider(gasPrice, gasLimit)

    init {
        instantiateWeb3J()
        web3j.ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
        initializeContract()
    }

    private fun instantiateWeb3J() {
        Log.d(
            TAG, try {
                web3j = Web3j.build(HttpService(ROPSTEN_INFURA_URL))
                "Connection Successful"
            } catch (e: Exception) {
                e.printStackTrace()
                "Connection Unsuccessful, error : ${e.message}"
            }
        )
    }

    private fun initializeContract() {
        Log.d(
            TAG, try {
                contract = VoteContractAccessor(
                    web3j,
                    RawTransactionManager(
                        web3j,
                        credentials,
                        ChainIdLong.ROPSTEN
                    ),
                    gasProvider, credentials
                )
                "Contract Initialized Successfully"
            } catch (e: Exception) {
                "Contract Initialization Error"
            }
        )
    }

    //------------------------------------------------------------------------------------------done
    fun partyVotesStatus(): String {
        return try {
            val votesForOne: Int =
                contract.getPartyVotes(PartyEnum.ONE).sendAsync().get().value.toInt()
            val votesForTwo: Int =
                contract.getPartyVotes(PartyEnum.TWO).sendAsync().get().value.toInt()
            val votesForThree: Int =
                contract.getPartyVotes(PartyEnum.THREE).sendAsync().get().value.toInt()
                    "party 1 votes = " + votesForOne +
                    "\nparty 2 votes = " + votesForTwo +
                    "\nparty 3 votes = " + votesForThree
        } catch (e: Exception) {
            e.printStackTrace()
            "error has occurred, ${e.message}"
        }
    }

    // TODO: this is the problem function...
    fun casteVote(party: PartyEnum): String {
        return try {
            Log.d(TAG, "started casteVote, Vote to be caste for ${party.name}")
            /**
             * this starts but never completes. No errors outputted.
             * If you watch closely, You'll see a '9' inside the get.
             * that is the timeout duration. This function times out
             * before being executed. It keeps waiting for whole 9
             * minutes and then outputs a timeout error
             */
            contract.putVote(party).sendAsync().get(9, TimeUnit.MINUTES).gasUsed.toString()
            /**
             * returns null as a result.
             */
        } catch (e: Exception) {
            e.printStackTrace()
            e.message.toString()
        }
    }

    fun getVoterAddresses(): String {
        return try {
            "voters = " + contract.getAddressValues().sendAsync().get().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "Error - ${e.message}"
        }
    }

    //------------------------------------------------------------------------------------------done
    fun getHasAlreadyVoted(): String {
        return try {
            contract.getHasAlreadyVoted().sendAsync().get().value.toString()
        } catch (e: Exception) {
            "Error has occurred while making calls :-\n${e.message}"
        }
    }

    fun getBalance(): BigDecimal = contract.printBalance()

    //checking connection for testing
    @RequiresApi(Build.VERSION_CODES.N)
    fun testingFunction() {
        contract.apply {
            Log.d(TAG, "Start testing-\n\n\nStarting function - 1")
            try {
                test1()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "finished test - 1,\n\n\nStarting function - 2")
            try {
                test2()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "finished test - 2,\n\n\nStarting function - 3")

            try {
                test3()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "finished test - 3")
        }
    }
}