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
    //------------------------------------------------------------------------------------------keys
    private val userPasswordKey = stringPreferencesKey("user_password")//------general-details
    private val userFullNameKey = stringPreferencesKey("user_preference")
    private val userEmailKey = stringPreferencesKey("user_email_key")
    private val userContactNumberKey = stringPreferencesKey("user_contact_number")
    private val userVotersIDKey = stringPreferencesKey("user_voter_id")//---documents-required
    private val userAdharNoKey = stringPreferencesKey("adhar_number")
    private val userUsesFingerprintKey = booleanPreferencesKey("user_uses_fingerprint")

    //-------------------------------------------------------------------------------------accessors

    val userPasswordFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userPasswordKey] ?: "" }

    val userFullNameFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userFullNameKey] ?: "" }

    val userEmailFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userEmailKey] ?: "" }

    val userContactNumberFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userContactNumberKey] ?: "" }

    val userVoterIDFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userVotersIDKey] ?: "" }

    val userAdharNoFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userAdharNoKey] ?: "" }

    val userUsesFingerprintFlow: Flow<Boolean> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userUsesFingerprintKey] ?: false }

    private fun catcherFun(throwable: Throwable) {
        if (throwable is IOException) {
            throwable.printStackTrace()
        } else {
            throw throwable
        }
    }

    //-------------------------------------------------------------------------------general-setters
    /**else section should be gray*/
    suspend fun dataStoreStringSetter(setFor: StringSetterEnum, stringValue: String, context: Context) {
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
    suspend fun dataStoreBooleanSetter(setFor: BooleanSetterEnum, booleanValue: Boolean, context: Context){
        context.datastore.edit {
            it[
                    when (setFor) {
                        BooleanSetterEnum.USER_USES_FINGERPRINT_KEY->userUsesFingerprintKey
                        else -> throw IllegalArgumentException("StringSetterEnum not registered for condition in data store setter")
                    }
            ] = booleanValue
        }}
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
    USER_USES_FINGERPRINT_KEY
}