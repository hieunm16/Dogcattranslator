package com.wa.dog.cat.sound.prank.model

import java.io.Serializable

data class SoundItem (
    var title: String? = null,
    var icon: Int? = null,
    var sound : String? = null
): Serializable {
    constructor():this(
    title = null,
    icon = null,
    sound = null
    )
}