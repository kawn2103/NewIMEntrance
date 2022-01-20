package kst.app.newimentrance.presentation.home.attendanceHistory

import kst.app.newimentrance.presentation.BasePresenter
import kst.app.newimentrance.presentation.BaseView

interface AttendanceHistoryContract {

    interface View: BaseView<Presenter> {

        fun finish()
    }

    interface Presenter: BasePresenter {

    }
}