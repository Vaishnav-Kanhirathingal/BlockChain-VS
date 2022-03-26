package com.kenetic.blockchainvs

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
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
    fun useAppContext() {
        val walletPublicKey = "0xE4e609e2E928E8F8b74C6Bb37e13503b337f8C70"
        val walletPrivateKey = "66c53799ee0c63f2564305e738ea7479d7aee84aed3aac4c01e54a7acbcc4d92"
        val emailOtp = "1111"
        val phoneOtp = "1111"
        val explanationInterval = 15000
        val waitInterval = 3000
        //-----------------------------------------------------------------------from-sign-in-prompt
        onView(withId(R.id.account_settings)).perform(click())
        onView(withId(R.id.new_user_button)).perform(click())
        //-----------------------------------------------------------------------from-sign-up-prompt
        //--------------------------------------------------------------------------------------name
        onView(withId(R.id.user_name_edit_text_text_field))
            .perform(typeText("First Middle Last"))
        //----------------------------------------------------------------------------------password
        onView(withId(R.id.user_set_password_edit_text_text_field))
            .perform(typeText("1234567890"))
        onView(withId(R.id.user_confirm_password_edit_text_text_field))
            .perform(typeText("1234567890"))
        //---------------------------------------------------------------------------------phone-otp
        repeat(2) {
            onView(withId(R.id.scroll_view)).perform(swipeUp())
            onView(withId(R.id.user_phone_number_edit_text_text_field))
                .perform(typeText("+12 1234567890"))
            // TODO: select otp generator
            onView(withId(R.id.user_phone_otp_edit_text_text_field))
                .perform(typeText(phoneOtp))
            onView(withId(R.id.verify_phone_otp)).perform(click())
            if (it != 2) {
                onView(withId(R.id.clear_phone_otp)).perform(click())
            }
        }
        //---------------------------------------------------------------------------------email-otp
        repeat(2) {
            onView(withId(R.id.scroll_view)).perform(swipeUp())
            onView(withId(R.id.user_email_edit_text_text_field))
                .perform(typeText("random.somebody@gmail.com"))
            // TODO: select otp generator
            onView(withId(R.id.user_email_otp_edit_text_text_field))
                .perform(typeText(emailOtp))
            onView(withId(R.id.verify_email_otp)).perform(click())
            if (it != 2) {
                onView(withId(R.id.clear_email_otp)).perform(click())
            }
            //---------------------------------------------------------------------------------adhar
            onView(withId(R.id.scroll_view)).perform(swipeUp())
            onView(withId(R.id.adhar_card_number_text_field))
                .perform(typeText("123456789012"))
            //------------------------------------------------------------------------------voter-id
            onView(withId(R.id.voter_id_text_field))
                .perform(typeText("sample12345"))
            //---------------------------------------------------------------------------fingerprint
            onView(withId(R.id.user_use_fingerprint)).check(matches(isNotChecked())).perform(
                click()
            )
            onView(withId(R.id.scroll_view)).perform(swipeUp())
            //--------------------------------------------------------------------------------wallet
            onView(withId(R.id.user_wallet_text_field)).perform(typeText(walletPublicKey))
            onView(withId(R.id.user_private_key_text_field)).perform(typeText(walletPrivateKey))
            //------------------------------------------------------------------------------register
            onView(withId(R.id.scroll_view)).perform(swipeUp())
            onView(withId(R.id.register_login)).perform(click())
        }
    }
}