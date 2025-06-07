package ir.mahan.histore.util

import ir.mahan.histore.util.constants.CATEGORY_LAPTOP
import ir.mahan.histore.util.constants.CATEGORY_MEN_SHOES
import ir.mahan.histore.util.constants.CATEGORY_MOBILE_PHONE
import ir.mahan.histore.util.constants.CATEGORY_STATIONERY

enum class ProductsCategories(val item: String) {
    MOBILE(CATEGORY_MOBILE_PHONE),
    SHOES(CATEGORY_MEN_SHOES),
    STATIONERY(CATEGORY_STATIONERY),
    LAPTOP(CATEGORY_LAPTOP)
}