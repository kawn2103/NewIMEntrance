package kst.app.newimentrance.presentation.home.attendanceHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kst.app.newimentrance.databinding.FragmentAttendanceHistoryBinding
import org.koin.android.scope.ScopeFragment

class AttendanceHistoryFragment : ScopeFragment(), AttendanceHistoryContract.View {

    override val presenter: AttendanceHistoryContract.Presenter by inject()

    private var binding: FragmentAttendanceHistoryBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAttendanceHistoryBinding.inflate(inflater)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun finish() {
    }

}