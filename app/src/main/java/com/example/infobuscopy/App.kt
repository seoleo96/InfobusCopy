package com.example.infobuscopy

import android.app.Application
import com.example.infobuscopy.data.network.InfoBusService
import com.example.infobuscopy.data.repository.MainRepository
import com.example.infobuscopy.presentation.screens.MainViewModel
import com.example.infobuscopy.util.LruCacheImpl
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class App : Application() {
    private val json = Json { ignoreUnknownKeys = true }

    override fun onCreate() {
        super.onCreate()
        startKoinMain()
    }

    private fun startKoinMain() {
        startKoin {
            androidLogger(level = Level.DEBUG)//todo
            androidContext(androidContext = applicationContext)
            modules(uiModule, dataModule)
        }
    }

    private var uiModule = module {
        viewModelOf(::MainViewModel)
        singleOf(::LruCacheImpl)
    }

    private val dataModule = module {
        single<Retrofit> { provideRetrofit() }
        single<InfoBusService> { provideInfoBusService(retrofit = get()) }

        singleOf(::MainRepository)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun provideRetrofit(): Retrofit {
        val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
            this.level = HttpLoggingInterceptor.Level.BASIC
            this.level = HttpLoggingInterceptor.Level.HEADERS
        }

        val client =  OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
            connectTimeout(15, TimeUnit.SECONDS)
            writeTimeout(15, TimeUnit.SECONDS)
            readTimeout(15, TimeUnit.SECONDS)
        }.build()
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    private fun provideInfoBusService(retrofit : Retrofit): InfoBusService {
        return retrofit.create(InfoBusService::class.java)
    }

    companion object {
        private const val BASE_URL = "https://infobus.kz/api/cities/23/routes/"
    }
}