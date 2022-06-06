package ru.vinyarsky.testapplication.di

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.vinyarsky.testapplication.data.NetworkService
import ru.vinyarsky.testapplication.data.RepositoryImpl
import ru.vinyarsky.testapplication.data.converters.ResponseConverter
import ru.vinyarsky.testapplication.data.converters.ResponseConverterImpl
import ru.vinyarsky.testapplication.domain.Interactor
import ru.vinyarsky.testapplication.domain.Repository
import ru.vinyarsky.testapplication.presentation.ViewModelFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("http://www.vinyarsky.ru")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor())
                .build()
        )
        .build();

    @Singleton
    @Provides
    fun networkService(retrofit: Retrofit): NetworkService =
        retrofit.create(NetworkService::class.java)

    @Singleton
    @Provides
    fun responseConverter(): ResponseConverter = ResponseConverterImpl()

    @Singleton
    @Provides
    fun repository(
        networkService: NetworkService,
        responseConverter: ResponseConverter
    ): Repository = RepositoryImpl(
        networkService = networkService,
        responseConverter = responseConverter
    )

    @Singleton
    @Provides
    fun interactor(
        repository: Repository
    ): Interactor = Interactor(
        repository = repository
    )

    @Singleton
    @Provides
    fun viewModelFactory(
        interactor: Interactor
    ): ViewModelProvider.Factory = ViewModelFactory(interactor)
}
