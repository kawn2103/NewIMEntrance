package kst.app.newimentrance.data.entity

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
        @SerializedName("code")
        val code: String? = null,
        @SerializedName("message")
        val message: String? = null,
        @SerializedName("center_id")
        val centerKey: String? = null
) : Parcelable
