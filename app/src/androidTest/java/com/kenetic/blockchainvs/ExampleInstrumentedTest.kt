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
class ExampleInstrumentedTest {
    @get:Rule
    val scenario = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun signUp() {
        val walletPrivateKey = "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
        val emailOtp = "1111"
        val phoneOtp = "1111"
        val explanationInterval: Long = 40000
        val waitInterval: Long = 3000
        //-----------------------------------------------------------------------from-sign-in-prompt
        onView(withId(R.id.account_settings)).perform(click())
        onView(withId(R.id.new_user_button)).perform(click())
        //-----------------------------------------------------------------------from-sign-up-prompt
        //--------------------------------------------------------------------------------------name
        onView(withId(R.id.user_name_edit_text_text_field)).perform(
            scrollTo(),
            typeText("First Middle Last")
        )
        //----------------------------------------------------------------------------------password
        onView(withId(R.id.user_set_password_edit_text_text_field)).perform(
            scrollTo(),
            typeText("1234567890")
        )
        onView(withId(R.id.user_confirm_password_edit_text_text_field)).perform(
            scrollTo(),
            typeText("1234567890")
        )
        //---------------------------------------------------------------------------------phone-otp
        repeat(2) {
            onView(withId(R.id.user_phone_number_edit_text_text_field)).perform(
                scrollTo(),
                typeText("+12 1234567890")
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
                typeText("random.somebody@gmail.com")
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
            scrollTo(), typeText("123456789012")
        )
        //----------------------------------------------------------------------------------voter-id
        onView(withId(R.id.voter_id_text_field)).perform(
            scrollTo(), typeText("sample1234")
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
}