package ir.mahan.histore.util.extensions

import ir.mahan.histore.util.views.TimeUtils


fun String.convertDateToFarsi(): String {
    val dateSplit = this
        .split("-")
        .map { it.toInt() }
    val timeUtils = TimeUtils(dateSplit[0], dateSplit[1], dateSplit[2])
    val iranianDate = timeUtils.iranianDate.split("/")
    val year = iranianDate[0]
    val month = iranianDate[1]
    val day = iranianDate[2]
    return "$year ${month.toInt().asMonthName()} $day"
}

fun Int.asMonthName(): String {
    val name = when (this) {
        1 -> "فروردین"
        2 -> "اردیبهشت"
        3 -> "خرداد"
        4 -> "تیر"
        5 -> "مرداد"
        6 -> "شهریور"
        7 -> "مهر"
        8 -> "آبان"
        9 -> "آذر"
        10 -> "دی"
        11 -> "بهمن"
        12 -> "اسفند"
        else -> ""
    }
    return name
}