package com.yapp.buddycon.navigation.gifticon

import com.yapp.buddycon.designsystem.R
import com.yapp.buddycon.navigation.base.BottomDestination
import com.yapp.buddycon.navigation.base.BuddyConDestination

private const val GIFTICON = "gifticon"
private const val GIFTICON_REGISTER = "gifticon_register"

sealed class GifticonDestination : BuddyConDestination {

    object Gifticon : GifticonDestination(), BottomDestination {
        override val route = GIFTICON
        override val drawableResId = R.drawable.ic_menu_gifticon
        override val drawableSelResId = R.drawable.ic_menu_gifticon_sel
    }

    object Register : GifticonDestination() {
        override val route = GIFTICON_REGISTER
    }
}
