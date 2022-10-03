package com.commoncents.di.module

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.commoncents.BuildConfig
import com.commoncents.data.api.ApiHelper
import com.commoncents.data.api.ApiHelperImpl
import com.commoncents.data.api.ApiService
import com.commoncents.preferences.Preferences
import com.commoncents.utils.Constants
import com.commoncents.utils.Logger.TAG
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext appContext: Context,
        userPreferences: Preferences,
    ) =
        run {
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original: Request = chain.request()
                    val request: Request = original.newBuilder()
                        .method(original.method, original.body)
                        .build()
                    var response: Response = chain.proceed(request)

                    try {
                        Log.e(TAG, "Before success url : " + request.url)
                        if (response.code == Constants.UnAuthorize) {
                            Log.e(TAG, "API Call")
                            runBlocking {
                                val retrofit =
                                    provideRetrofit(OkHttpClient(), BuildConfig.BASE_URL)
                                val call = retrofit.create(ApiService::class.java)
                                    .refreshToken(userPreferences.getUserData().refreshToken!!)

                                Log.e(TAG, "isSuccessful :-->" + call.body().toString())
                                Log.e(TAG, "isSuccessful :-->" + call.isSuccessful)

                                if (call.isSuccessful) {
                                    if (call.body()?.code == Constants.SuccessCode) {
                                        Log.e(TAG, "API isSuccessful")
                                        Log.e(
                                            TAG,
                                            "token response" + Gson().toJson(call.body())
                                        )
                                        userPreferences.saveUserDetail(
                                            call.body()!!.data!!,
                                            Constants.UnAuthorize
                                        )
                                        response = chain.proceed(request)
                                        Log.e(TAG, "after success url" + request.url)
                                    } else {
                                        Log.e(TAG, "API false")
                                        Constants.logout(appContext, userPreferences)
                                    }
                                } else {
                                    Constants.logout(appContext, userPreferences)
                                    Log.e(TAG, "API False")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                    response
                }
                .addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().also {
                        val token = runBlocking { userPreferences.getUserData().accessToken }
                        if (!TextUtils.isEmpty(token)) {
                            it.addHeader(
                                "Authorization", "Bearer $token"
                            )
                        }
                    }.build())
                }
                .also { client ->
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                    client.connectTimeout(30, TimeUnit.SECONDS)
                    client.readTimeout(30, TimeUnit.SECONDS)
                    client.writeTimeout(30, TimeUnit.SECONDS)
                    client.cookieJar(SessionCookieJar()).build()
                }.build()
        }

    private class SessionCookieJar : CookieJar {
        private var cookies: List<Cookie>? = null
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            if (url.encodedPath.endsWith("login")) {
                this.cookies = ArrayList(cookies)
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return if (!url.encodedPath.endsWith("login") && cookies != null) {
                return cookies!!
            } else Collections.emptyList()
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        BASE_URL: String
    ): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().serializeNulls().create()
                )
            )
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper
}
