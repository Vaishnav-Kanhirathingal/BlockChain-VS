package com.kenetic.blockchainvs.datapack.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
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
    //----------------------------------------------------------------------------------------string
    private val userPasswordKey = stringPreferencesKey("user_password")
    private val userFullNameKey = stringPreferencesKey("user_preference")
    private val userEmailKey = stringPreferencesKey("user_email_key")
    private val userContactNumberKey = stringPreferencesKey("user_contact_number")
    private val userVotersIDKey = stringPreferencesKey("user_voter_id")
    private val userAdharNoKey = stringPreferencesKey("adhar_number")
    private val userPrivateKeyKey = stringPreferencesKey("user_privat_key_key")

    //---------------------------------------------------------------------------------------boolean
    private val userUsesFingerprintKey = booleanPreferencesKey("user_uses_fingerprint")
    private val userLoggedInKey = booleanPreferencesKey("user_logged_in")
    private val userRegisteredKey = booleanPreferencesKey("user_registered")

    // TODO: remove after testing...
    private val balanceKey = stringPreferencesKey("user_balance")
    private val v1Key = intPreferencesKey("vo_1")
    private val v2Key = intPreferencesKey("vo_2")
    private val v3Key = intPreferencesKey("vo_3")
    private val hvKey = booleanPreferencesKey("h_v")

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
    val userPrivatKeyFlow: Flow<String> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userPrivateKeyKey] ?: default }

    //--------------------------------------------------------------------------------------booleans
    val userUsesFingerprintFlow: Flow<Boolean> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userUsesFingerprintKey] ?: false }
    val userLoggedInFlow: Flow<Boolean> =
        context.datastore.data.catch { catcherFun(it) }.map { it[userLoggedInKey] ?: false }
    val userRegisteredFlow =
        context.datastore.data.catch { catcherFun(it) }.map { it[userRegisteredKey] ?: false }

    // TODO: remove after testing
    val balanceFlow =
        context.datastore.data.catch { catcherFun(it) }.map { it[balanceKey] ?: default }
    val v1Flow = context.datastore.data.catch { catcherFun(it) }.map { it[v1Key] ?: 0 }
    val v2Flow = context.datastore.data.catch { catcherFun(it) }.map { it[v2Key] ?: 0 }
    val v3Flow = context.datastore.data.catch { catcherFun(it) }.map { it[v3Key] ?: 0 }
    val hvFlow = context.datastore.data.catch { catcherFun(it) }.map { it[hvKey] ?: false }

    private fun catcherFun(throwable: Throwable) {
        if (throwable is IOException) {
            throwable.printStackTrace()
        } else {
            throw throwable
        }
    }

    //-------------------------------------------------------------------------------general-setters
    suspend fun dataStoreStringSetter(
        setFor: StringSetterEnum,
        stringValue: String,
        context: Context
    ) {
        context.datastore.edit {
            it[
                    when (setFor) {
                        StringSetterEnum.USER_FULL_NAME_KEY -> userFullNameKey
                        StringSetterEnum.USER_PASSWORD_KEY -> userPasswordKey
                        StringSetterEnum.USER_EMAIL_KEY -> userEmailKey
                        StringSetterEnum.USER_CONTACT_NUMBER_KEY -> userContactNumberKey
                        StringSetterEnum.USER_VOTERS_ID_KEY -> userVotersIDKey
                        StringSetterEnum.USER_ADHAR_NO_KEY -> userAdharNoKey
                        StringSetterEnum.USER_PRIVATE_KEY_KEY -> userPrivateKeyKey
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

    // TODO: for testing purposes
    suspend fun testSetter(
        context: Context,
        testEnum: TestEnum,
        v1v2v3: Int,
        bal: String,
        hv: Boolean
    ) {
        context.datastore.edit {
            when (testEnum) {
                TestEnum.BALANCE -> it[balanceKey] = bal
                TestEnum.V1 -> it[v1Key] = it[v1Key]!! + v1v2v3
                TestEnum.V2 -> it[v2Key] = it[v2Key]!! + v1v2v3
                TestEnum.V3 -> it[v3Key] = it[v3Key]!! + v1v2v3
                TestEnum.HV -> it[hvKey] = hv
                else -> throw IllegalArgumentException("not a registered test enum")
            }
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
    USER_PRIVATE_KEY_KEY
}

enum class BooleanSetterEnum {
    USER_USES_FINGERPRINT_KEY,
    USER_LOGGED_IN,
    USER_REGISTERED
}

// TODO: for testing purposes
enum class TestEnum {
    BALANCE, V1, V2, V3, HV
}