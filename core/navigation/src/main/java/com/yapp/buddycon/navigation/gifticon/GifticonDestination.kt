package com.yapp.buddycon.navigation.gifticon

import com.yapp.buddycon.designsystem.R
import com.yapp.buddycon.navigation.base.BottomDestination
import com.yapp.buddycon.navigation.base.CurtainCallDestination

const val GIFTICON = "gifticon"

sealed interface GifticonDestination : CurtainCallDestination {

    object Gifticon : GifticonDestination, BottomDestination {
        override val route = GIFTICON
        override val drawableResId = R.drawable.ic_menu_gifticon
        override val drawableSelResId = R.drawable.ic_menu_gifticon_sel
    }
}
