# BlockChain-VS

AS OF 13-04-22, THIS PROJECT IS FUNCTIONAL.

FILES USED FOR CONTRACT INTERACTION ARE -

THIS FILE IS RESPONSIBLE FOR ALL THE MAIN TRANSACTIONS
app/src/main/java/com/kenetic/blockchainvs/contract_classes/VoteContractDelegate.kt

THESE FILES CONTAIN THE AUTO-GENERATED WEB3 FILES FOR JAVA MADE USING THE WEB3 LIBRARY:

1] app/src/main/java/com/kenetic/blockchainvs/contract_classes/contract_auto_generate/ContractAutoGenTesting.java
    THIS CONTRACT ACCEPTS MULTIPLE VOTES

2] app/src/main/java/com/kenetic/blockchainvs/contract_classes/contract_auto_generate/ContractAutoGenOriginal.java
    THIS CONTRACT DOES NOT ACCEPT MULTIPLE VOTES

THIS FILE CONTAINS THE FUNCTION ENCODERS FOR STATE CHANGING FUNCTIONS. THESE WERE GENERATED TO COUNTER FAULTY WEB3J FUNCTIONS WHICH WERE UNABLE TO PERFORM STATE CHANGING TRANSACTIONS ON THE TESTNET SERVERS.
app/src/main/java/com/kenetic/blockchainvs/contract_classes/AlternateTransactionHandler.java

THIS FILE IS A TEST CLASS WHICH AUTOMATES THE SIGNUP PROCESS.
app/src/main/java/com/kenetic/blockchainvs/contract_classes/AlternateTransactionHandler.java

## Sample screen shots / Guide -

### Sign up screen -

Here, the user can sign up using their email and phone number. This page is for demo purpose only, programatically it doesn't create an account remotely yet. However, it does store the details locally.

![Screenshot_20220719_230453_com kenetic blockchainvs](https://user-images.githubusercontent.com/94210466/179814908-4d390c72-78ce-4da6-a1b5-2e1c6fdf8306.jpg)

### Login Screen -

this is the login screen, use the regustered email and password to login to the account. The sign Up process is discussed later on.

![Screenshot_20220719_191447_com kenetic blockchainvs](https://user-images.githubusercontent.com/94210466/179783970-134c3b4d-8ccf-4024-9cd3-1c4395858c67.jpg)

### The main screen's side menu -

here, you can find the options for the account settings such as `Log In`, `Log Out`, `Sign Up`, `Remove Account`, `Switch Account`. Below this you will find the `Contract Interface`. This will be discussed later on.

![Screenshot_20220719_231212_com kenetic blockchainvs](https://user-images.githubusercontent.com/94210466/179815128-f3d0414f-23b4-4495-b4af-47a2d085ff62.jpg)

### The Contract Interface -

This page requires you to be signed in. Sign-in in can be done using either the regisered email and password, or your fingerprint of enabled. Once signed in, you can Access the entire solidity contract which has already been set up in the ropsten test network. Transactions that require a fee will ask for fingerprint verification.

![Screenshot_20220719_192524_com kenetic blockchainvs](https://user-images.githubusercontent.com/94210466/179812778-1f60bd7b-a40f-47de-a448-ac4a3a84657f.jpg)


