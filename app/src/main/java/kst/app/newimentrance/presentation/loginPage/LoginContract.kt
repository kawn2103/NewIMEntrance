package kst.app.newimentrance.presentation.loginPage

import kst.app.newimentrance.presentation.BasePresenter
import kst.app.newimentrance.presentation.BaseView

interface LoginContract {

    interface View: BaseView<Presenter> {

        // 로딩 인디케이터 실행
        fun showLoadingIndicator(msg: String)

        // 로딩 인디케이터 종료
        fun hideLoadingIndicator()

        // 에러 메시지
        fun errorMessage(msg: String)

        // 메인뷰 화면 이동
        fun transAttendanceView(centerKey: String)
    }

    interface Presenter: BasePresenter {

        // 로그인 실행
        fun requestLogin(id: String, pw: String)

    }
}