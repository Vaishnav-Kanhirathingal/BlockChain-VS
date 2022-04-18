package com.kenetic.blockchainvs.networking_api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

// TODO: replace this with ip-address of server
private val BASE_URL = "http://mywebsite.com/"

//private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

private val retrofit =
    Retrofit
        .Builder()
        .addConverterFactory(
            MoshiConverterFactory
                .create(
                    Moshi
                        .Builder()
                        .add(KotlinJsonAdapterFactory())
                        .build()
                )
        )
        .baseUrl(BASE_URL)
        .build()

interface VoteNetworkingApiService {
    // TODO: replace link segments
    //-------------------------------------------------------------------------------------------otp
    @GET("phone=1234567890")
    suspend fun getPhoneOtp(phoneNumber: String): String

    @GET("email=sample.email@gmail.com")
    suspend fun getEmailOtp(email: String): String

    //----------------------------------------------------------------------------------user-details
    @GET("phone-number")
    suspend fun sendAndVerifyUserDetails(UserDetailsInJsonFormat: String): String

    //------------------------------------------------------------------------------------------vote
    @GET("phone-number")
    suspend fun registerUserVote(): String

    @GET("party-names")
    suspend fun getPartyNames(): String
}

object VoteNetworkApi {
    val retrofitService: VoteNetworkingApiService by lazy {
        retrofit.create(VoteNetworkingApiService::class.java)
    }
}

