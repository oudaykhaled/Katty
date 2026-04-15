package com.catfact.app.core.domain.model

import androidx.compose.runtime.Stable

@Stable
enum class FactCategory {
    Short,
    Medium,
    Long;

    companion object {
        private const val SHORT_THRESHOLD = 50
        private const val MEDIUM_THRESHOLD = 120

        fun fromLength(length: Int): FactCategory = when {
            length <= SHORT_THRESHOLD -> Short
            length <= MEDIUM_THRESHOLD -> Medium
            else -> Long
        }
    }
}
