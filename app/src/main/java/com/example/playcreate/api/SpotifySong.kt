package com.example.playcreate.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.text.clearSpans
import androidx.core.text.toSpannable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


data class SpotifySong (

    @SerializedName("name")
    val key: String

) {
    companion object {

    }
}