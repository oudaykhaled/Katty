package com.catfact.app.core.designsystem.util

import androidx.annotation.StringRes
import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class Raw(val value: String) : UiText
    data class Resource(@StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText
}

@Composable
// Android's formatted-string API requires a vararg array.
@Suppress("SpreadOperator")
fun UiText.asString(): String = when (this) {
    is UiText.Raw -> value
    is UiText.Resource -> stringResource(resId, *args.toTypedArray())
}

// Side effects resolve messages against the current configuration-aware Resources instance.
@Suppress("SpreadOperator")
fun UiText.resolve(resources: Resources): String = when (this) {
    is UiText.Raw -> value
    is UiText.Resource -> resources.getString(resId, *args.toTypedArray())
}
