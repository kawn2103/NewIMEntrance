package kst.app.newimentrance

import android.app.Application
import kst.app.newimentrance.di.*
import org.koin.android.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class NewIMEntranceApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        // 앱 실행 시 코인 시작
        startKoin {
            androidLogger(
                if (BuildConfig.DEBUG){
                    Level.DEBUG
                } else {
                    Level.NONE
                }
            )
            androidContext(this@NewIMEntranceApplication)
            modules(appModule + netWorkModule + dataModule + domainModule + presenterModule)
        }
    }
}