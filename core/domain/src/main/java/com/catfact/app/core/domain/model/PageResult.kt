package com.catfact.app.core.domain.model

data class PageResult(
    val facts: List<CatFact>,
    val currentPage: Int,
    val lastPage: Int
) {
    val hasMorePages: Boolean get() = currentPage < lastPage
}
