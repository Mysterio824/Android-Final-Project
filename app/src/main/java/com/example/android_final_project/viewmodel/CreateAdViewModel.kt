init {
    viewModelScope.launch {
        try {
            val user = loadCurrentUser()
            if (user != null) {
                updateEndDate(_state.value.durationDays)
                loadUserAds()
            } else {
                _state.update { it.copy(error = "Failed to load user. Please try again.") }
            }
        } catch (e: Exception) {
            _state.update { it.copy(error = "Failed to initialize: ${e.message}") }
        }
    }
}

private suspend fun loadCurrentUser(): User? {
    return try {
        val user = authService.getCurrentUser()
        Log.d("CREATEADDDDD", "${user?.id}")
        _state.update { it.copy(currentUser = user) }
        user
    } catch (e: Exception) {
        _state.update { it.copy(error = "Failed to load user: ${e.message}") }
        null
    }
}

private fun updateEndDate(days: Int) {
    val currentTime = System.currentTimeMillis()
    val endDate = Date(currentTime + (days * 24 * 60 * 60 * 1000))
    _state.update { it.copy(endDate = endDate) }
}

private suspend fun loadUserAds() {
    try {
        _state.update { it.copy(isLoadingAds = true, error = null) }
        val currentUser = _state.value.currentUser
        if (currentUser == null) {
            _state.update { 
                it.copy(
                    isLoadingAds = false,
                    error = "User not found. Please try again.",
                    userAds = emptyList()
                )
            }
        } else {
            // ... existing code ...
        }
    } catch (e: Exception) {
        _state.update { 
            it.copy(
                isLoadingAds = false,
                error = "Failed to load ads: ${e.message}",
                userAds = emptyList()
            )
        }
    }
} 