package ru.vinyarsky.testapplication.data.models

import com.google.gson.annotations.SerializedName

data class NetworkResponse(

    val products: List<Item>?
) {
    data class Item(

        @SerializedName("id")
        val id: Int?,

        @SerializedName("name")
        val name: String?,

        @SerializedName("description")
        val description: String?,

        @SerializedName("price")
        val price: Price?,

        @SerializedName("available")
        val available: Boolean?,

        @SerializedName("rating")
        val rating: Float?,
    )

    data class Price(

        @SerializedName("value")
        val value: Float?,

        @SerializedName("currency")
        val currency: PriceCurrency?
    )

    enum class PriceCurrency {

        @SerializedName("EUR")
        EUR,

        @SerializedName("USD")
        USD,

        @SerializedName("RUB")
        RUB
    }
}
