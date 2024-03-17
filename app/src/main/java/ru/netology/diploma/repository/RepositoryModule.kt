package ru.netology.diploma.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.diploma.repository.PostRepository
import ru.netology.diploma.repository.PostRepositoryImpl
import javax.inject.Singleton


@InstallIn (SingletonComponent::class)
@Module
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsPostRepository (impl: PostRepositoryImpl) : PostRepository
}