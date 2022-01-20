package kst.app.newimentrance.data.preference

import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferenceManager(
        //di로 주입될 파라미터들
        private val sharedPreferences: SharedPreferences
) : PreferenceManager {
    // 자동 로그인을 위한 센터 ID,PW 쉐어드 저장
    override fun putAccount(centerId: String, centerPw: String) =
        sharedPreferences.edit {
            putString("centerId", centerId)
            putString("centerPw", centerPw)
        }

    // 자동 로그인을 위한 센터 ID,PW 쉐어드 호출 *Pair 사용*
    // pair랑 각각의 데이터를 쌍으로 만들어서 인스턴스화 하는것
    override fun getAccount(): Pair<String?, String?> {

        val centerId = sharedPreferences.getString("centerId", "-1")
        val centerPw = sharedPreferences.getString("centerPw", "-1")

        return Pair(centerId, centerPw)
    }

}