package com.wa.dog.cat.sound.prank.model

import java.io.Serializable

data class ScreenItem(
    var title: String? = null,
    var description: String? = null,
    var screenImg: Int? = null
): Serializable {
    constructor():this(
        title = null,
        description = null,
        screenImg = null
    )
}