package idv.hsu.authenticator.domain

import idv.hsu.authenticator.data.DataStorePreferences
import idv.hsu.authenticator.data.TotpRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class DomainModule {
    @Single
    fun provideDeleteAccountUseCase(repository: TotpRepository): DeleteAccountUseCase =
        DeleteAccountUseCase(repository)

    @Single
    fun provideGetAccountUseCase(repository: TotpRepository): GetAccountUseCase =
        GetAccountUseCase(repository)

    @Single
    fun provideGetAllAccountsUseCase(repository: TotpRepository): GetAllAccountsUseCase =
        GetAllAccountsUseCase(repository)

    @Single
    fun provideGetFirstAccountUseCase(preferences: DataStorePreferences): GetFirstTimeOpenUseCase =
        GetFirstTimeOpenUseCase(preferences)

    @Single
    fun provideInsertAccountUseCase(repository: TotpRepository): InsertAccountUseCase =
        InsertAccountUseCase(repository)

    @Single
    fun provideSetFirstAccountUseCase(preferences: DataStorePreferences): SetFirstTimeOpenUseCase =
        SetFirstTimeOpenUseCase(preferences)
}