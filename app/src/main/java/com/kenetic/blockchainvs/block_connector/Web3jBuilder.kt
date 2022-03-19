package com.kenetic.blockchainvs.block_connector

import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService


class Web3jBuilder {
    var web3 = Web3j.build(HttpService("https://ropsten.infura.io/v3/c358089e1aaa4746aa50e61d4ec41c5c"))

    //val web3 = Web3j.build(HttpService()) // defaults to http://localhost:8545/
    val web3ClientVersion = web3.web3ClientVersion().send()
    val clientVersion = web3ClientVersion.web3ClientVersion
}