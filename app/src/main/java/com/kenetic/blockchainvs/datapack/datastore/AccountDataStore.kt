package com.kenetic.blockchainvs.datapack.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.datastore: DataStore<Preferences> by preferencesDataStore(
    name = "account_data_store"
)

class AccountDataStore(context: Context) {
    val default = "UNSPECIFIED"

    //------------------------------------------------------------------------------------------keys
    private val userPasswordKey = stringPreferencesKey("user_password")//------general-details
    private val userFullNameKey = stringPreferencesKey("user_preference")
    private val userEmailKey = stringPreferencesKey("user_email_key")
    private val userContactNumberKey = stringPreferencesKey("user_contact_number")
    private val userVotersIDKey = stringPreferencesKey("user_voter_id")//---documents-required
    private val userAdharNoKey = stringPreferencesKey("adhar_number")
    private val userUsesFingerprintKey = booleanPreferencesKey("user_uses_fingerprint")
    private val userLoggedInKey = booleanPreferencesKey("user_logged_in")
    private val userRegisteredKey = booleanPreferencesKey("user_registered")

    //------------------------------------------------------------------------------detail-accessors
    //---------------------------------------------------------------------------------------strings
    val userPasswordFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userPasswordKey] ?: default }
    val userFullNameFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userFullNameKey] ?: default }
    val userEmailFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userEmailKey] ?: default }
    val userContactNumberFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userContactNumberKey] ?: default }
    val userVoterIDFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userVotersIDKey] ?: default }
    val userAdharNoFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userAdharNoKey] ?: default }

    //--------------------------------------------------------------------------------------booleans
    val userUsesFingerprintFlow: Flow<Boolean> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userUsesFingerprintKey] ?: false }
    val userLoggedInFlow: Flow<Boolean> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userLoggedInKey] ?: false }
    val userRegisteredFlow =
        context.datastore.data.catch { catcherFun(it) }.map { it[userRegisteredKey] ?: false }

    private fun catcherFun(throwable: Throwable) {
        if (throwable is IOException) {
            throwable.printStackTrace()
        } else {
            throw throwable
        }
    }

    //-------------------------------------------------------------------------------general-setters
    // TODO: check if the else section is gray
    suspend fun dataStoreStringSetter(
        setFor: StringSetterEnum,
        stringValue: String,
        context: Context
    ) {
        context.datastore.edit {
            it[
                    when (setFor) {
                        StringSetterEnum.USER_PASSWORD_KEY -> userPasswordKey
                        StringSetterEnum.USER_FULL_NAME_KEY -> userFullNameKey
                        StringSetterEnum.USER_EMAIL_KEY -> userEmailKey
                        StringSetterEnum.USER_CONTACT_NUMBER_KEY -> userContactNumberKey
                        StringSetterEnum.USER_VOTERS_ID_KEY -> userVotersIDKey
                        StringSetterEnum.USER_ADHAR_NO_KEY -> userAdharNoKey
                        else -> throw IllegalArgumentException("StringSetterEnum not registered for condition in data store setter")
                    }
            ] = stringValue
        }
    }

    suspend fun dataStoreBooleanSetter(
        setFor: BooleanSetterEnum,
        booleanValue: Boolean,
        context: Context
    ) {
        context.datastore.edit {
            it[
                    when (setFor) {
                        BooleanSetterEnum.USER_USES_FINGERPRINT_KEY -> userUsesFingerprintKey
                        BooleanSetterEnum.USER_LOGGED_IN -> userLoggedInKey
                        BooleanSetterEnum.USER_REGISTERED -> userRegisteredKey
                        else -> throw IllegalArgumentException("StringSetterEnum not registered for condition in data store setter")
                    }
            ] = booleanValue
        }
    }

    suspend fun resetAccounts(context: Context) {
        context.datastore.edit {
            //--------------------------------------------------------------------------------string
            it[userPasswordKey] = default
            it[userFullNameKey] = default
            it[userEmailKey] = default
            it[userContactNumberKey] = default
            it[userVotersIDKey] = default
            it[userAdharNoKey] = default
            //-------------------------------------------------------------------------------boolean
            it[userUsesFingerprintKey] = false
            it[userLoggedInKey] = false
            it[userRegisteredKey] = false
        }
    }
}

enum class StringSetterEnum {
    USER_PASSWORD_KEY,
    USER_FULL_NAME_KEY,
    USER_EMAIL_KEY,
    USER_CONTACT_NUMBER_KEY,
    USER_VOTERS_ID_KEY,
    USER_ADHAR_NO_KEY,
}

enum class BooleanSetterEnum {
    USER_USES_FINGERPRINT_KEY,
    USER_LOGGED_IN,
    USER_REGISTERED
}