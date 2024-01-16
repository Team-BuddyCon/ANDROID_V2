package com.yapp.buddycon.domain.model.gifticon

import com.yapp.buddycon.domain.model.type.GifticonCategory
import com.yapp.buddycon.domain.model.type.GifticonStoreCategory

data class AvailableGifticon(
    val availableGifticons: List<AvailableGifticonInfo>,
    val isFirstPage: Boolean = false,
    val isLastPage: Boolean = false,
    val pageNumber: Int = 0,
) {
    data class AvailableGifticonInfo(
        val imageUrl: String = "",
        val category: GifticonCategory = GifticonCategory.ETC,
        val storeCategory: GifticonStoreCategory = GifticonStoreCategory.OTHERS,
        val name: String = "",
        val expireDate: String = "",
    )
}