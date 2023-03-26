package com.google.ar.core.examples.navigation.data

import com.google.ar.core.Anchor

/**
 * Created by Ashwani Kumar Singh on 15,March,2023.
 */
data class WrappedAnchor(
    val anchor: Anchor,
    var distance: Float,
    var direction: String
)