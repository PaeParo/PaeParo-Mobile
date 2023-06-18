package com.paeparo.paeparo_mobile.model


import com.squareup.moshi.Json

data class NaverResponse(
    @Json(name = "addresses") val addresses: List<Addresse?>?,
    @Json(name = "errorMessage") val errorMessage: String?,
    @Json(name = "meta") val meta: Meta?,
    @Json(name = "status") val status: String?
) {
    data class Addresse(
        @field:Json(name = "addressElements") val addressElements: List<AddressElement?>?,
        @field:Json(name = "distance") val distance: Double?,
        @field:Json(name = "englishAddress") val englishAddress: String?,
        @field:Json(name = "jibunAddress") val jibunAddress: String?,
        @field:Json(name = "roadAddress") val roadAddress: String?,
        @field:Json(name = "x") val x: String?,
        @field:Json(name = "y") val y: String?
    ) {
        data class AddressElement(
            @field:Json(name = "code") val code: String?,
            @field:Json(name = "longName") val longName: String?,
            @field:Json(name = "shortName") val shortName: String?,
            @field:Json(name = "types") val types: List<String?>?
        )
    }

    data class Meta(
        @Json(name = "count") val count: Int?,
        @Json(name = "page") val page: Int?,
        @Json(name = "totalCount") val totalCount: Int?
    )
}