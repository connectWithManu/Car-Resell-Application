package com.manu.carstoreuser.model

data class CarModel (
    var carId: String? = null,
    var coverImg: String? = null,
    var title: String? = null,
    var year: String? = null,
    var rating: String? = null,
    var description: String? = null,
    var price: String? = null,
    var location: String? = null,
    var brand: String? = null,
    var insurance: String? = null,
    var runKm: String? = null,
    var engineType: String? = null,
    var gearType: String? = null,
    var rtoNum: String? = null,
    var selectCategory: String? = null,
    var seating: String? = null,
    var available: String? = null,
    var tag: String? = null,
    var carImages: ArrayList<String>? = null
)
