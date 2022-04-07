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

    private val walletPrivateKey =
        "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
    private val emailOtp = "1111"
    private val phoneOtp = "1111"
    private val explanationInterval: Long = 20000
    private val waitInterval: Long = 3000

    //-------------------------------------------------------------------------------sign-up-details
    private val fullName = "First Middle Last"
    private val password = "1234567890"
    private val phoneNumber = "+12 1234567890"
    private val emailAccount = "random.somebody@gmail.com"
    private val adharId = "123456789012"
    private val voterId = "sample1234"

    @Test
    fun signUp() {
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
            scrollTo(), typeText(walletPrivateKey)
        )
        //----------------------------------------------------------------------------------register
        onView(withId(R.id.register_login)).perform(
            scrollTo(), click()
        )
        Thread.sleep(explanationInterval)
    }

    @Test
    fun signInAndInteractWithContract() {
        Thread.sleep(waitInterval)
        //-----------------------------------------------------------------------------------sign-in
        onView(withId(R.id.account_settings)).perform(click())
        onView(withId(R.id.user_name_text_field)).perform(typeText(fullName))
        onView(withId(R.id.user_set_password_text_field)).perform(typeText(password))
        onView(withId(R.id.confirm_login)).perform(click())
        Thread.sleep(explanationInterval)

        onView(withContentDescription("Navigation Content")).perform(click())
        //----------------------------------------------------------------------------------interact
        onView(withId(R.id.contract_interface)).perform(scrollTo(), click())
        onView(withId(R.id.get_registered_voters_button)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)

        onView(withId(R.id.check_voter_status_button)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)

        onView(withId(R.id.get_votes_button)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)

        onView(withId(R.id.get_balance_button)).perform(scrollTo(), click())
        Thread.sleep(waitInterval)

        Thread.sleep(explanationInterval)
    }
}