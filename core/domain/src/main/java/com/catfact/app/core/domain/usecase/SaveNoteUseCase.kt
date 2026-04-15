package com.catfact.app.core.domain.usecase

import com.catfact.app.core.domain.repository.CatFactRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val repository: CatFactRepository
) {
    suspend operator fun invoke(factId: String, note: String) {
        repository.updateNote(factId, note)
    }
}
