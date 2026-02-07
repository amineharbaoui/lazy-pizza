package com.example.core.ui.utils.formatting

fun String.formatOrderNumber(): String = "#${this.substring(0, 3)}-${this.substring(3)}"
