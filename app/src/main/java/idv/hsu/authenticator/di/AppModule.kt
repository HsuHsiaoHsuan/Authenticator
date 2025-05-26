package idv.hsu.authenticator.di

import idv.hsu.authenticator.data.DataModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [DataModule::class])
@ComponentScan("idv.hsu.authenticator")
class AppModule