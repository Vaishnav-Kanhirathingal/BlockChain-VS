package com.kenetic.blockchainvs

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val TAG = "ExampleInstrumentedTest"

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedAlternateTransactionHandler {
    @get:Rule
    val scenario = ActivityScenarioRule(MainActivity::class.java)

    //-----------------------------------------------------------------------------------private-key

    private val emailOtp = "1111"
    private val phoneOtp = "1111"
    private val explanationInterval: Long = 10000
    private val waitInterval: Long = 3000
    private val transactionInterval: Long = 45000

    //-------------------------------------------------------------------------------sign-up-details
    private val accountOnePrivateKey =
        "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
    private val accountTwoPrivateKey =
        "6653ef960205a8584f91526da6dbeafb9b1b3e1813811c7783e34ef979e85fef"

    private lateinit var accountPrivateKey: String
    private lateinit var fullName: String
    private lateinit var password: String
    private lateinit var phoneNumber: String
    private lateinit var emailAccount: String
    private lateinit var adharId: String
    private lateinit var voterId: String

    private fun useAccountOne() {
        accountPrivateKey = accountOnePrivateKey
        fullName = "Vaishnav Prashant Kanhirathingal"
        password = "0000000001"
        phoneNumber = "+12 1111111111"
        emailAccount = "vaishnav.kanhira@gmail.com"
        adharId = "123456123456"
        voterId = "voter11111"
    }

    private fun useAccountTwo() {
        accountPrivateKey = accountTwoPrivateKey
        fullName = "Adil Jalaluddin Khan"
        password = "0000000002"
        phoneNumber = "+12 2222222222"
        emailAccount = "adil.khan@gmail.com"
        adharId = "654321654321"
        voterId = "voter22222"
    }

    @Test
    fun signUp() {
        //----------------------------------------------------------------------------choose-account
        useAccountOne()
        Thread.sleep(explanationInterval)
        //-----------------------------------------------------------------------from-sign-in-prompt
        onView(withId(R.id.account_settings)).perform(click())
        onView(withId(R.id.new_user_button)).perform(click())
        //-----------------------------------------------------------------------from-sign-up-prompt
        //--------------------------------------------------------------------------------------name
        onView(withId(R.id.user_name_edit_text_text_field)).perform(
            scrollTo(),
            typeText(fullName)
        )
        //----------------------------------------------------------------------------------password
        onView(withId(R.id.user_set_password_edit_text_text_field)).perform(
            scrollTo(),
            typeText(password)
        )
        onView(withId(R.id.user_confirm_password_edit_text_text_field)).perform(
            scrollTo(),
            typeText(password)
        )
        //---------------------------------------------------------------------------------phone-otp
        repeat(2) {
            onView(withId(R.id.user_phone_number_edit_text_text_field)).perform(
                scrollTo(),
                typeText(phoneNumber)
            )
            onView(withId(R.id.user_phone_otp_edit_text_text_field)).perform(scrollTo())
            onView(withContentDescription(R.string.phone_otp_description)).perform(click())
            onView(withId(R.id.user_phone_otp_edit_text_text_field)).perform(typeText(phoneOtp))
            onView(withId(R.id.verify_phone_otp)).perform(click())
            if (it == 0) {
                onView(withId(R.id.clear_phone_otp)).perform(scrollTo(), click())
            }
        }
        //---------------------------------------------------------------------------------email-otp
        repeat(2) {
            onView(withId(R.id.user_email_edit_text_text_field)).perform(
                scrollTo(),
                typeText(emailAccount)
            )
            onView(withId(R.id.user_email_otp_edit_text_text_field)).perform(scrollTo())
            onView(withContentDescription(R.string.email_otp_description)).perform(click())
            onView(withId(R.id.user_email_otp_edit_text_text_field)).perform(typeText(emailOtp))
            onView(withId(R.id.verify_email_otp)).perform(scrollTo(), click())
            if (it == 0) {
                onView(withId(R.id.clear_email_otp)).perform(scrollTo(), click())
            }
        }
        //-------------------------------------------------------------------------------------adhar
        onView(withId(R.id.adhar_card_number_text_field)).perform(
            scrollTo(), typeText(adharId)
        )
        //----------------------------------------------------------------------------------voter-id
        onView(withId(R.id.voter_id_text_field)).perform(
            scrollTo(), typeText(voterId)
        )
        //-------------------------------------------------------------------------------fingerprint
        onView(withId(R.id.user_use_fingerprint)).check(matches(isNotChecked())).perform(
            scrollTo(), click()
        )
        //------------------------------------------------------------------------------------wallet
        onView(withId(R.id.user_private_key_text_field)).perform(
            scrollTo(), typeText(accountPrivateKey)
        )
        Thread.sleep(explanationInterval)
        //----------------------------------------------------------------------------------register
        onView(withId(R.id.register_login)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)
    }

    @Test
    fun signInAndInteractWithContract() {
        useAccountOne()
        Thread.sleep(waitInterval)
        //-----------------------------------------------------------------------------------sign-in
        onView(withId(R.id.account_settings)).perform(click())
        onView(withId(R.id.user_name_text_field)).perform(typeText(fullName))
        onView(withId(R.id.user_set_password_text_field)).perform(typeText(password))
        onView(withId(R.id.confirm_login)).perform(click())
        Thread.sleep(explanationInterval)

        //---------------------------------------------------------------------------------side-menu
        onView(withContentDescription("Navigation Content")).perform(click())
        onView(withId(R.id.navigation_view_main_screen)).perform(swipeUp())
        onView(withId(R.id.contract_interface)).perform(click())

        //----------------------------------------------------------------------------------contract

        onView(withId(R.id.party_two_radio_button)).perform(scrollTo(), click())
        onView(withId(R.id.caste_vote_button)).perform(scrollTo(), click())
        Thread.sleep(transactionInterval)

        onView(withId(R.id.get_registered_voters_button)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)

        onView(withId(R.id.check_voter_status_button)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)

        onView(withId(R.id.get_votes_button)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)

        onView(withId(R.id.add_to_voters_list_button)).perform(scrollTo(), click())
        Thread.sleep(transactionInterval)

        onView(withId(R.id.get_balance_button)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)
    }
}