package kst.app.newimentrance.data.entity


import com.google.gson.annotations.SerializedName

data class TestResponse(
    @SerializedName("code")
    val code: String?,
    @SerializedName("data")
    val `data`: List<Data>?,
    @SerializedName("message")
    val message: String?
)