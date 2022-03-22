package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.util.Log
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger
import java.util.concurrent.TimeUnit


private const val TAG = "VoteContractDelegate"

class VoteContractDelegate() {
    //-----------------------------------------------------------------------------contract-elements
    // TODO: check these values
    private val MINIMUM_GAS_LIMIT = 30000
    private val MAX_GAS_LIMIT: BigInteger = BigInteger.valueOf(3000000)//-------------self-added

    private val PRIVATE_KEY_ROPSTEN = "c9852fcf061b47c58d5294cd7a23548c"
    private val ROPSTEN_INFURA_URL = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"


    //    private val credentials = WalletUtils.loadCredentials("qISTALO-42", "0xE4e609e2E928E8F8b74C6Bb37e13503b337f8C70")
    private val credentials = Credentials.create(PRIVATE_KEY_ROPSTEN)

    private lateinit var web3j: Web3j//--------------------------------------------works-as-intended
    private lateinit var contract: VoteContractAccessor

    private lateinit var transactionManager: TransactionManager
    private val gasProvider: ContractGasProvider = DefaultGasProvider()

    init {
        instantiateWeb3J()
        transactionManager = RawTransactionManager(web3j, credentials)
        initializeContract()
    }

    private fun instantiateWeb3J() {// TODO: first
        try {
            web3j = Web3j.build(HttpService(ROPSTEN_INFURA_URL))
            Log.d(TAG, "Connection Successful")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "Connection Unsuccessful, error : ${e.message}")
        }
    }

    private fun initializeContract() {// TODO: second
        try {
            contract = VoteContractAccessor(web3j, transactionManager, gasProvider)
            Log.d(TAG, "Contract Initialized Successfully")
        } catch (e: Exception) {
            Log.d(TAG, "Contract Initialization Error")
        }
    }

    //------------------------------------------------------------------------visual-type-check-done
    fun partyVotesStatus(): String {
        return try {
            // TODO: get values of votes and add them to the string
            val votesForOne: Uint256 = contract.getPartyVotes(PartyEnum.ONE).sendAsync().get()
            val votesForTwo: Uint256 = contract.getPartyVotes(PartyEnum.TWO).sendAsync().get()
            val votesForThree: Uint256 = contract.getPartyVotes(PartyEnum.THREE).sendAsync().get()
            "party votes status: " +
                    "\nparty one votes = " + votesForOne +
                    "\nparty one votes = " + votesForTwo +
                    "\nparty one votes = " + votesForThree
        } catch (e: Exception) {
            e.printStackTrace()
            "error has occurred, ${e.message}"
        }
    }

    //------------------------------------------------------------------------visual-type-check-done
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
            contract.getAddressValues().sendAsync().get().typeAsString
        } catch (e: Exception) {
            e.printStackTrace()
            "Error has occurred while receiving address list - ${e.message}"
        }
    }

    fun getHasAlreadyVoted(): Boolean {
        // TODO: change this
        return try {
            contract.getHasAlreadyVoted().sendAsync().get().value
        } catch (e: Exception) {
            // TODO: check if to change according to error
            true
        }
    }

    //checking connection for testing
    fun abc() {
        try{
            val privateKeyPersonal =
                "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
            val web3: Web3j =
                Web3j.build(HttpService("https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"))
            val cred = Credentials.create(privateKeyPersonal)
            val transactionManager2 = RawTransactionManager(web3, cred)
            val gasProvider = DefaultGasProvider()
            val accessor = VoteContractAccessor(web3j, transactionManager2, gasProvider)
            Log.d(
                TAG,
                "the returned output = ${
                    accessor.getPartyVotes(PartyEnum.ONE).sendAsync().get().value
                } Transaction successful"
            )
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}