package idv.hsu.authenticator

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import idv.hsu.authenticator.utils.SecretKeyUtils
import timber.log.Timber

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

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