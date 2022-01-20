package kst.app.newimentrance.presentation.home.attendanceHistory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class AttendanceHistoryPresenter(
    private val view: AttendanceHistoryContract.View
) : AttendanceHistoryContract.Presenter {

    override val scope: CoroutineScope = MainScope()

    override fun onViewCreated() {
    }

    override fun onDestroyView() {}
}