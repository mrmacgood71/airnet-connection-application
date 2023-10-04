package it.macgood.connectionapplication.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.macgood.connectionapplication.data.api.AirnetApi
import it.macgood.connectionapplication.data.repository.ConnectionRepositoryImpl
import it.macgood.connectionapplication.domain.repository.ConnectionRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("baseUrl")
    fun provideBaseUrl() = AirnetApi.BASE_URL

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
    ) = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()



    @Provides
    @Singleton
    fun provideAirnetApi(
        client: OkHttpClient,
        @Named("baseUrl") baseUrl: String
    ): AirnetApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AirnetApi::class.java)

    @Provides
    @Singleton
    fun provideConnectionRepository(
        api: AirnetApi,
        @ApplicationContext context: Context
    ): ConnectionRepository = ConnectionRepositoryImpl(api, context)
}