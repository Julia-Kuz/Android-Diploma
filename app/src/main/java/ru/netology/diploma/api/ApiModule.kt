package ru.netology.diploma.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.netology.diploma.auth.AppAuth
import javax.inject.Singleton

@InstallIn(SingletonComponent :: class)
@Module
class ApiModule {

    companion object {

        private const val BASE_URL = "http://94.228.125.136:8080/api/"
    }


    @Singleton
    @Provides
    fun provideLogger (): HttpLoggingInterceptor = HttpLoggingInterceptor().apply { //устанавливаем уровень логирования
        level = HttpLoggingInterceptor.Level.BODY
    }


    @Singleton
    @Provides
    fun provideOkHttp (
        logger: HttpLoggingInterceptor,
        appAuth: AppAuth
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->     //добавляем еще один перехватчик, для добавления token из авторизации в запросы
            appAuth.authStateFlow.value.token?.let { token -> //обращаемся к св-ву authStateFlow (синглтона AppAuth), где хранятся id & token текущего пользователя, берем token
                val newRequest = chain.request().newBuilder()       //создаем новый запрос
                    .addHeader("Authorization", token)                //и в этот запрос добавляем заголовок "Authorization"
                    .build()
                return@addInterceptor chain.proceed(newRequest) //этот новый запрос отправить дальше в обработку
            }
            chain.proceed(chain.request())          //если token не было, продолжаем обработку с исходным запросом
        }
        .addInterceptor(logger) //этот клиент передаем в retrofit - ставим после первого, чтобы заголовки "Authorization" уже выводились в logcat
        .build()

    @Singleton
    @Provides
    fun provideRetrofit (
        okhttp: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okhttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun providePostApiService (
        retrofit: Retrofit
    ) : PostsApiService = retrofit.create()

    @Singleton
    @Provides
    fun provideAuthApiService (
        retrofit: Retrofit
    ) : AuthApiService = retrofit.create()

    @Singleton
    @Provides
    fun provideEventApiService (
        retrofit: Retrofit
    ) : EventApiService = retrofit.create()

}