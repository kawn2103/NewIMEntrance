package kst.app.newimentrance.presentation.loginPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import kst.app.newimentrance.databinding.ActivityLoginBinding
import kst.app.newimentrance.presentation.home.HomeViewActivity
import org.koin.android.scope.ScopeActivity

class LoginActivity : ScopeActivity(),LoginContract.View{

    // 로그인 뷰 바인딩
    private val binding: ActivityLoginBinding by lazy {ActivityLoginBinding.inflate(layoutInflater)}

    // presenter DI
    override val presenter: LoginContract.Presenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        loginEvent()
        presenter.onViewCreated()
    }

    // 뷰 선언
    private fun initView(){
        // 레이아웃의 백그라운드 설정을 위에 얻어진 뷰에도 적용
        binding.loginBackgroundLayout.apply {
            measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED)
            clipToOutline = true
        }
    }

    // 로그인 버튼 클릭 이벤트
    private fun loginEvent(){
        // 로그인 버튼 클릭 리스터
        binding.loginButton.setOnClickListener {
            // 에딧 텍스트에 입력된 ID, PW 변수 저장
            val loginId = binding.idEditText.text.toString()
            val loginPw = binding.passwordEditText.text.toString()
            // di로 주입받은 프레젠터의 로그인 이벤트로 전송
            presenter.requestLogin(loginId,loginPw)
        }
    }

    // 로딩 인디케이터 실행
    override fun showLoadingIndicator(msg: String) {
        binding.disableView.isVisible = true
        binding.messageTextView.text = msg
    }

    // 로딩 인디케이터 종료
    override fun hideLoadingIndicator() {
        binding.disableView.isVisible = false
    }

    // 에러 메시지 세팅
    override fun errorMessage(msg: String) {
        binding.errorMessageTextView.text = msg
    }

    // 메인 뷰 화면이동
    override fun transAttendanceView(centerKey: String) {
        val intent = Intent(this,HomeViewActivity::class.java)
        // 인텐트의 가독성을 높이기 위해 .apply를 사용하기도 함
        intent.apply {
            this.putExtra("center_key", centerKey)
        }
        startActivity(intent)
    }
}