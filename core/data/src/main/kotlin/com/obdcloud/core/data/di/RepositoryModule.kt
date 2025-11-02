package com.obdcloud.core.data.di

import com.obdcloud.core.data.repository.AdapterRepositoryImpl
import com.obdcloud.core.data.repository.DiagnosticsRepositoryImpl
import com.obdcloud.core.domain.repository.AdapterRepository
import com.obdcloud.core.domain.repository.DiagnosticsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAdapterRepository(
        adapterRepositoryImpl: AdapterRepositoryImpl
    ): AdapterRepository
    
    @Binds
    @Singleton
    abstract fun bindDiagnosticsRepository(
        diagnosticsRepositoryImpl: DiagnosticsRepositoryImpl
    ): DiagnosticsRepository
}