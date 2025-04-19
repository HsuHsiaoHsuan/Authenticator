package idv.hsu.authenticator

import android.app.Application
import android.util.Log
import idv.hsu.authenticator.di.AppModule
import idv.hsu.authenticator.utils.SecretKeyUtils
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(AppModule().module)
        }

        if (!SecretKeyUtils.isKeyGenerated()) {
            SecretKeyUtils.generateKey()
        }

        if (resources.getBoolean(R.bool.isDebug)) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, "FREEMAN_$tag", message, t)
                }
            })
        } else {
            Timber.plant(CrashReportingTree());
        }
    }

    private class CrashReportingTree : Timber.Tree() {
        protected override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }

//            FakeCrashLibrary.log(priority, tag, message)
//            if (t != null) {
//                if (priority == Log.ERROR) {
//                    FakeCrashLibrary.logError(t)
//                } else if (priority == Log.WARN) {
//                    FakeCrashLibrary.logWarning(t)
//                }
//            }
        }
    }
}