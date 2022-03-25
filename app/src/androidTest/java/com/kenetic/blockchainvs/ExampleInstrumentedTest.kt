package com.kenetic.blockchainvs

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Rule
    val scenario = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
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
            onView(withId(R.id.user_phone_number_edit_text_text_field))
                .perform(typeText("+12 1234567890"))
            // TODO: select otp generator
            onView(withId(R.id.user_phone_otp_edit_text_text_field))
                .perform(typeText("123456"))
            onView(withId(R.id.verify_phone_otp)).perform(click())
            if (it != 2) {
                onView(withId(R.id.clear_phone_otp)).perform(click())
            }
        }
        //---------------------------------------------------------------------------------email-otp
        repeat(2) {
            onView(withId(R.id.user_email_edit_text_text_field))
                .perform(typeText("random.somebody@gmail.com"))
            // TODO: select otp generator
            onView(withId(R.id.user_email_otp_edit_text_text_field))
                .perform(typeText("123456"))
            onView(withId(R.id.verify_email_otp)).perform(click())
            if (it != 2) {
                onView(withId(R.id.clear_email_otp)).perform(click())
            }
            //---------------------------------------------------------------------------------adhar
            onView(withId(R.id.adhar_card_number_text_field))
                .perform(typeText("123456789012"))
            //------------------------------------------------------------------------------voter-id
            onView(withId(R.id.voter_id_text_field))
                .perform(typeText("sample12345"))
            //------------------------------------------------------------------------------register
            onView(withId(R.id.register_login)).perform(click())
        }
    }
}