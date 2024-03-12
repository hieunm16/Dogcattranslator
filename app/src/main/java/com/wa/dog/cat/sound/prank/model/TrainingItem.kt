package com.wa.dog.cat.sound.prank.model

import java.io.Serializable

data class TrainingItem  (
    var title: String? = null,
    var icon: Int? = null,
    var des : String? = null
): Serializable {
    constructor():this(
        title = null,
        icon = null,
        des = null
    )
}