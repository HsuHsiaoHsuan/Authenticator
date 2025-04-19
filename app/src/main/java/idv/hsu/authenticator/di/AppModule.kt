package idv.hsu.authenticator.di

import android.content.Context
import idv.hsu.authenticator.data.DataModule
import idv.hsu.authenticator.data.DataStorePreferences
import idv.hsu.authenticator.domain.DeleteAccountUseCase
import idv.hsu.authenticator.domain.DomainModule
import idv.hsu.authenticator.domain.GetAccountUseCase
import idv.hsu.authenticator.domain.GetAllAccountsUseCase
import idv.hsu.authenticator.domain.GetFirstTimeOpenUseCase
import idv.hsu.authenticator.domain.InsertAccountUseCase
import idv.hsu.authenticator.domain.SetFirstTimeOpenUseCase
import idv.hsu.authenticator.presentation.viewmodel.FirstTimeOpenViewModel
import idv.hsu.authenticator.presentation.viewmodel.MainViewModel
import idv.hsu.authenticator.presentation.viewmodel.QrCodeReaderViewModel
import idv.hsu.authenticator.presentation.viewmodel.TotpViewModel
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(includes = [DomainModule::class, DataModule::class])
@ComponentScan("idv.hsu.authenticator")
class AppModule {
    @Single
    fun provideDataStorePreferences(
        context: Context
    ): DataStorePreferences = DataStorePreferences(context)

    @Single
    fun provideGetFirstTimeOpenUseCase(
        preferences: DataStorePreferences
    ): GetFirstTimeOpenUseCase = GetFirstTimeOpenUseCase(preferences)

    @Single
    fun provideViewModel(
        getFirstTimeOpenUseCase: GetFirstTimeOpenUseCase,
        setFirstTimeOpenUseCase: idv.hsu.authenticator.domain.SetFirstTimeOpenUseCase
    ): FirstTimeOpenViewModel = FirstTimeOpenViewModel(
        getFirstTimeOpenUseCase,
        setFirstTimeOpenUseCase
    )

    @Single
    fun provideMainViewModel(
        insertAccountUseCase: InsertAccountUseCase,
        getFirstTimeOpenUseCase: GetFirstTimeOpenUseCase,
        setFirstTimeOpenUseCase: SetFirstTimeOpenUseCase,
    ): MainViewModel = MainViewModel(
        insertAccountUseCase,
        getFirstTimeOpenUseCase,
        setFirstTimeOpenUseCase
    )

    @Single
    fun provideQrCodeReaderViewModel(
        deleteAccountUseCase: DeleteAccountUseCase,
        getAccountUseCase: GetAccountUseCase,
        getAllAccountsUseCase: GetAllAccountsUseCase,
        insertAccountUseCase: InsertAccountUseCase
    ): QrCodeReaderViewModel = QrCodeReaderViewModel(
        deleteAccountUseCase,
        getAccountUseCase,
        getAllAccountsUseCase,
        insertAccountUseCase
    )

    @Single
    fun provideTotpViewModel(
        insertAccountUseCase: InsertAccountUseCase,
        getAllAccountsUseCase: GetAllAccountsUseCase,
        deleteAccountUseCase: DeleteAccountUseCase,
    ): TotpViewModel = TotpViewModel(
        insertAccountUseCase,
        getAllAccountsUseCase,
        deleteAccountUseCase
    )
}