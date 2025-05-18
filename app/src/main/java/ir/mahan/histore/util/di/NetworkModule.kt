package ir.mahan.histore.util.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.mahan.histore.data.api.APIEndpoints
import ir.mahan.histore.data.datastore.SessionManager
import ir.mahan.histore.util.constants.ACCEPT
import ir.mahan.histore.util.constants.APPLICATION_JSON
import ir.mahan.histore.util.constants.AUTHORIZATION
import ir.mahan.histore.util.constants.BASE_URL
import ir.mahan.histore.util.constants.BEARER
import ir.mahan.histore.util.constants.CONNECTION_TIME
import ir.mahan.histore.util.constants.NAMED_PING
import ir.mahan.histore.util.constants.PING_INTERVAL
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String, gson: Gson, client: OkHttpClient): APIEndpoints =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(APIEndpoints::class.java)

    @Provides
    @Singleton
    fun provideBaseUrl() = BASE_URL

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideClient(
        timeout: Long, @Named(NAMED_PING) ping: Long, session: SessionManager,
        interceptor: HttpLoggingInterceptor
    ) = OkHttpClient.Builder().addInterceptor { chain ->
        // with chain we can add headers
        val token = runBlocking {
            // using run blocking: (it runs on MAIN) to get token from datastore
            session.authToken.first().toString()
        }
        chain.proceed(
            chain.request().newBuilder().also {
                it.addHeader(AUTHORIZATION, "$BEARER $token")
                it.addHeader(ACCEPT, APPLICATION_JSON)
            }.build()
        )
    }.also { client ->
        client.addInterceptor(interceptor)
    }
        .writeTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .pingInterval(ping, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideInterceptor() = HttpLoggingInterceptor().apply {
        //level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideTimeout() = CONNECTION_TIME

    @Provides
    @Singleton
    @Named(NAMED_PING)
    fun providePingInterval() = PING_INTERVAL
}