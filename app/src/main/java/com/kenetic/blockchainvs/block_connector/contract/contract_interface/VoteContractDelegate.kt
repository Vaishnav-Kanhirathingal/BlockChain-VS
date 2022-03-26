package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.util.Log
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.ChainIdLong
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.tx.gas.StaticGasProvider
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.TimeUnit

private const val TAG = "VoteContractDelegate"

class VoteContractDelegate() {
    //-----------------------------------------------------------------------------contract-elements
    private val USER_PRIVATE_KEY =
        "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
    private val ROPSTEN_INFURA_URL = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"

    private val credentials = Credentials.create(USER_PRIVATE_KEY)

    private lateinit var web3j: Web3j//--------------------------------------------works-as-intended
    private lateinit var contract: VoteContractAccessor

    private val gasProvider: ContractGasProvider = DefaultGasProvider()

    // TODO: change this
    private val newGasProvider: ContractGasProvider = StaticGasProvider(
        BigInteger.valueOf(3000000),
        BigInteger.valueOf(3000000)
    )

    init {
        instantiateWeb3J()
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
                    RawTransactionManager(web3j, credentials, ChainIdLong.ROPSTEN),
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
            "party votes status: " +
                    "\nparty one votes = " + votesForOne +
                    "\nparty one votes = " + votesForTwo +
                    "\nparty one votes = " + votesForThree
        } catch (e: Exception) {
            e.printStackTrace()
            "error has occurred, ${e.message}"
        }
    }

    // TODO: make changes
    fun casteVote(party: PartyEnum): Int {
        return try {
            contract
                .putVote(party)
                .sendAsync()
                .get(3, TimeUnit.MINUTES)
                .gasUsed
                .toInt()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            0
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
    fun testingFunction() {
        contract.apply {
            Log.d(TAG, "Start testing-\n\n\nStarting new function")
            try {
                test1()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "finished test - 1,\n\n\nStarting the next function")
            try {
                test2()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "finished test - 2,\n\n\nStarting the next function")
            try {
                test3()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "finished test - 3,\n\n\nStarting the next function")
            try {
                test4()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.d(TAG, "finished test - 4\n\n\nBalance -")
            try {
                printBalance()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}