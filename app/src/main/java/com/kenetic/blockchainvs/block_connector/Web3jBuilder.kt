package com.kenetic.blockchainvs.block_connector

import org.web3j.abi.FunctionEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.RemoteCall
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Contract
import org.web3j.tx.TransactionManager
import java.math.BigInteger
import java.util.*


class Web3jBuilder {
    var web3 =
        Web3j.build(HttpService("https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"))
}