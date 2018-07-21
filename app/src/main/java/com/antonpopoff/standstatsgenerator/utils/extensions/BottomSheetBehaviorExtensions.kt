package com.antonpopoff.standstatsgenerator.utils.extensions

import android.support.design.widget.BottomSheetBehavior

fun BottomSheetBehavior<*>.collapsed() = this.state == BottomSheetBehavior.STATE_COLLAPSED

fun BottomSheetBehavior<*>.expanded() = this.state == BottomSheetBehavior.STATE_EXPANDED

fun BottomSheetBehavior<*>.collapse() {
    this.state = BottomSheetBehavior.STATE_COLLAPSED
}

fun BottomSheetBehavior<*>.expand() {
    this.state = BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetBehavior<*>.toggle() {
    if (this.expanded()) {
        this.collapse()
    } else {
        this.expand()
    }
}
