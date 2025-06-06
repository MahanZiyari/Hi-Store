package ir.mahan.histore.util.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ir.mahan.histore.data.model.login.BodyLogin

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @Provides
    fun provideBodyLogin() = BodyLogin()
}