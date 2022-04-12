# BlockChain-VS

BlockChain VS

AS OF 13-04-22, THIS PROJECT IS FUNCTIONAL.

FILES USED FOR CONTRACT INTERACTION ARE -

app/src/main/java/com/kenetic/blockchainvs/block_connector/contract/contract_interface/VoteContractDelegate.kt
	THIS FILE IS RESPONSIBLE FOR ALL THE MAIN TRANSACTIONS

app/src/main/java/com/kenetic/blockchainvs/block_connector/contract/contract_interface/ContractHex.java
	THIS FILE CONTAINS THE AUTO-GENERATED WEB3 FILES FOR JAVA MADE USING THE WEB3 LIBRARY

app/src/main/java/com/kenetic/blockchainvs/block_connector/contract/contract_interface/AlternateTransactionHandler.java
	THIS FILE CONTAINS THE FUNCTION ENCODERS FOR STATE CHANGING FUNCTIONS. THESE WERE GENERATED TO
	COUNTER FAULTY WEB3J FUNCTIONS WHICH WERE UNABLE TO PERFORM STATE CHANGING TRANSACTIONS ON THE
	TESTNET SERVERS.

app/src/androidTest/java/com/kenetic/blockchainvs/ExampleInstrumentedAlternateTransactionHandler.kt
	THIS FILE IS A TEST CLASS WHICH AUTOMATES THE SIGNUP PROCESS.(NOT NECESSARY)