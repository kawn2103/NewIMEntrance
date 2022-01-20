package kst.app.newimentrance.di

import android.app.Activity
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kst.app.newimentrance.BuildConfig
import kst.app.newimentrance.data.api.ImarkerApi
import kst.app.newimentrance.data.api.Url
import kst.app.newimentrance.data.preference.PreferenceManager
import kst.app.newimentrance.data.preference.SharedPreferenceManager
import kst.app.newimentrance.data.repository.LoginRepository
import kst.app.newimentrance.data.repository.LoginRepositoryImpl
import kst.app.newimentrance.presentation.home.HomeViewActivity
import kst.app.newimentrance.presentation.home.attendanceHistory.AttendanceHistoryContract
import kst.app.newimentrance.presentation.home.attendanceHistory.AttendanceHistoryFragment
import kst.app.newimentrance.presentation.home.attendanceHistory.AttendanceHistoryPresenter
import kst.app.newimentrance.presentation.loginPage.LoginActivity
import kst.app.newimentrance.presentation.loginPage.LoginContract
import kst.app.newimentrance.presentation.loginPage.LoginPresenter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// 앱 공통 모듈
val appModule = module {
    // 비동기 처리를 위한 Coroutine
    single { Dispatchers.IO }
}

// 네트워크 관리를 위한 모듈 (retrofit2)
val netWorkModule = module {
    single {
        OkHttpClient()
                .newBuilder()
                .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            level = if (BuildConfig.DEBUG) {
                                HttpLoggingInterceptor.Level.BODY
                            } else {
                                HttpLoggingInterceptor.Level.NONE
                            }
                        }
                )
                .build()
    }

    single<ImarkerApi> {
        Retrofit.Builder().baseUrl(Url.base_url)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(get())
                .build()
                .create()
    }
}

// 데이터 관리를 위한 모듈 (파이어베이스, API, SharedPreferences, repository 등)
val dataModule = module {

    // Preference
    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }


    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get())}
}

// UseCase 관리 모듈
val domainModule = module {

}

// View 화면 관리 모듈
val presenterModule = module {
    //로그인 화면 DI 설정
    scope<LoginActivity>{
        scoped<LoginContract.Presenter>{LoginPresenter(getSource(),get())}
    }
    scope<AttendanceHistoryFragment> {
        scoped<AttendanceHistoryContract.Presenter> { AttendanceHistoryPresenter(getSource()) }
    }
}