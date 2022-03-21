package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import androidx.lifecycle.MutableLiveData
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

class VoteContractDelegate() {
    //-----------------------------------------------------------------------------contract-elements
    // TODO: check these values
    private val MINIMUM_GAS_LIMIT = 30000
    private val MAX_GAS_LIMIT: BigInteger = BigInteger.valueOf(3000000)//-------------self-added
    private val PRIVATE_KEY_ROPSTEN = "c9852fcf061b47c58d5294cd7a23548c"
    private val ROPSTEN_INFURA_URL = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"

    //----------------------------------------------------------------------contract-output-livedata
    private val unFetched = "Value Not Fetched Yet"
    private val addressValues = MutableLiveData(unFetched)
    private val partyOneVotes = MutableLiveData(unFetched)
    private val partyTwoVotes = MutableLiveData(unFetched)
    private val partyThreeVotes = MutableLiveData(unFetched)

    private val credentials = Credentials.create(PRIVATE_KEY_ROPSTEN)

    private lateinit var web3j: Web3j
    private lateinit var contract: VoteContractAccessor

    //------------------------------------------------------------------------------------------self
    private lateinit var transactionManager: TransactionManager
    private val gasProvider: ContractGasProvider = DefaultGasProvider()

    init {
        instantiateWeb3J()
        transactionManager = RawTransactionManager(web3j, credentials)
        initializeContract()
    }

    private fun instantiateWeb3J(): String {// TODO: first
        return try {
            web3j = Web3j.build(HttpService(ROPSTEN_INFURA_URL))
            "Connection Successful"
        } catch (e: Exception) {
            e.printStackTrace()
            "Connection Unsuccessful, error : ${e.message}"
        }
    }

    private fun initializeContract(): String {// TODO: second
        return try {
            contract = VoteContractAccessor(web3j, transactionManager, gasProvider)
            "Contract Initialized Successfully"
        } catch (e: Exception) {
            "Contract Initialization Error"
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
        } catch (e:Exception){
            // TODO: check if to change according to error
            true
        }
    }
}