package com.kenetic.blockchainvs.block_connector.contract.contract_interface

import android.util.Log
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

private const val TAG = "VoteContractDelegate"

class VoteContractDelegate() {
    //-----------------------------------------------------------------------------contract-elements
    // TODO: get this from data store
    private val USER_PRIVATE_KEY =
        "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
    private val ROPSTEN_INFURA_URL = "https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"
    private val CONTRACT_ADDRESS = "0x84D46ba7aAac6221DF9038d3Ccf41F1cd46001aF"

    private val credentials = Credentials.create(USER_PRIVATE_KEY)

    private val web3j: Web3j = Web3j.build(HttpService(ROPSTEN_INFURA_URL))
    //-----------------------------------------------------------------------------works-as-intended

    // TODO: set correct value
    private val gasPrice: BigInteger = Convert.toWei("40", Convert.Unit.GWEI).toBigInteger()

    //this gas limit value is from deployment and has to be constant
    private val gasLimit = BigInteger.valueOf(40000)

    private val gasProvider: ContractGasProvider = StaticGasProvider(gasPrice, gasLimit)
    private var newContract: ContractHex =
        ContractHex.load(
            CONTRACT_ADDRESS,
            web3j,
            RawTransactionManager(web3j, credentials, ChainIdLong.ROPSTEN),
            gasProvider
        )

    //------------------------------------------------------------------------------------------done
    fun partyVotesStatus(): String {
        return try {
            val votesForOne: Int =
                newContract.party1Votes.send().toInt()
            val votesForTwo: Int =
                newContract.party2Votes.send().toInt()
            val votesForThree: Int =
                newContract.party3Votes.send().toInt()
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
            newContract.registerVote(
                BigInteger.valueOf(
                    when (party) {
                        PartyEnum.ONE -> 1
                        PartyEnum.TWO -> 2
                        PartyEnum.THREE -> 3
                    }
                )
            ).send().gasUsed.toString()
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
            val x = newContract.addressValues.send().toString()
            "voters = $x"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error - ${e.message}"
        }
    }

    //------------------------------------------------------------------------------------------done
    fun getHasAlreadyVoted(): String {
        return try {
            newContract.hasAlreadyVoted().send().toString()
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