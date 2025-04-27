package com.androidfinalproject.hacktok.ui.createAd

import android.net.Uri
import com.androidfinalproject.hacktok.model.TargetAudience
import com.androidfinalproject.hacktok.model.enums.AdType

sealed class CreateAdAction {
    data class UpdateAdContent(val content: String) : CreateAdAction()
    data class UpdateAdMedia(val url: String) : CreateAdAction()
    data class UpdateAdUrl(val url: String) : CreateAdAction()
    data class SelectAdType(val adType: AdType) : CreateAdAction()
    data class UpdateDuration(val days: Int) : CreateAdAction()
    data class UpdateAgeRange(val min: Int, val max: Int) : CreateAdAction()
    data class AddInterest(val interest: String) : CreateAdAction()
    data class RemoveInterest(val interest: String) : CreateAdAction()
    data class AddLocation(val location: String) : CreateAdAction()
    data class RemoveLocation(val location: String) : CreateAdAction()
    data class UpdateTargetAudience(val audience: TargetAudience) : CreateAdAction()
    data class UploadImage(val imageUri: Uri) : CreateAdAction()
    data object SubmitAd : CreateAdAction()
    data object NavigateBack : CreateAdAction()
    data object LoadUserAds : CreateAdAction()
    data class DeleteAd(val adId: String) : CreateAdAction()
}