package com.example.mycatapp
import com.google.gson.annotations.SerializedName

data class NetCat(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
    @SerializedName("breeds") val breeds: List<Any>,
    @SerializedName("categories") val categories: List<Any>
): Any() { //모든 클래스는 암묵적으로 Any 를 상속받고있다 'ㅁ')/
    override fun toString(): String {
        return "NetCat(id='$id', url='$url', breeds=$breeds, categories=$categories)"
    }
}