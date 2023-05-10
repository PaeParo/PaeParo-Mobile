package com.paeparo.paeparo_mobile.model.KakaoMapModel


import com.squareup.moshi.Json

data class KaKaoResponse(
    @Json(name = "documents")
    val documents: List<Document?>?,
    @Json(name = "meta")
    val meta: Meta?
) {
    data class Document(
        @Json(name = "address_name")
        val addressName: String?, // 서울 강남구 삼성동 159
        @Json(name = "category_group_code")
        val categoryGroupCode: String?,
        @Json(name = "category_group_name")
        val categoryGroupName: String?,
        @Json(name = "category_name")
        val categoryName: String?, // 가정,생활 > 문구,사무용품 > 디자인문구 > 카카오프렌즈
        @Json(name = "distance")
        val distance: String?, // 418
        @Json(name = "id")
        val id: String?, // 26338954
        @Json(name = "phone")
        val phone: String?, // 02-6002-1880
        @Json(name = "place_name")
        val placeName: String?, // 카카오프렌즈 스타필드 코엑스몰점
        @Json(name = "place_url")
        val placeUrl: String?, // http://place.map.kakao.com/26338954
        @Json(name = "road_address_name")
        val roadAddressName: String?, // 서울 강남구 영동대로 513
        @Json(name = "x")
        val x: String?, // 127.059028716089
        @Json(name = "y")
        val y: String? // 37.5120756277877
    )

    data class Meta(
        @Json(name = "is_end")
        val isEnd: Boolean?, // false
        @Json(name = "pageable_count")
        val pageableCount: Int?, // 21
        @Json(name = "same_name")
        val sameName: SameName?,
        @Json(name = "total_count")
        val totalCount: Int? // 21
    ) {
        data class SameName(
            @Json(name = "keyword")
            val keyword: String?, // 카카오프렌즈
            @Json(name = "region")
            val region: List<Any?>?,
            @Json(name = "selected_region")
            val selectedRegion: String?
        )
    }
}