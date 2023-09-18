package com.kwasowski.sportslife.extensions

import android.content.res.Resources


val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
