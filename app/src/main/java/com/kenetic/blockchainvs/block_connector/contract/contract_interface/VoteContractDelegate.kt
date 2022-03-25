package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.util.Log
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
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

    private lateinit var transactionManager: TransactionManager
    private val gasProvider: ContractGasProvider = DefaultGasProvider()

    init {
        instantiateWeb3J()
        transactionManager = RawTransactionManager(web3j, credentials)
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
                contract = VoteContractAccessor(web3j, transactionManager, gasProvider)
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

    // TODO: change set return type to array
    fun getVoterAddresses(): String {
        return try {
            "voters = " + contract.getAddressValues().sendAsync().get().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "Error has occurred while receiving address list - ${e.message}"
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

    //checking connection for testing
    fun testingFunction() {
        try {
            contract.apply {
                test6()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}