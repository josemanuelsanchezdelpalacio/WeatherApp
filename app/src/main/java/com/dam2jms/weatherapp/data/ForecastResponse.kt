package com.dam2jms.weatherapp.data

import com.google.gson.annotations.SerializedName

/**para representar la informacion del pronostico del clima de los siguientes dias**/
data class ForecastResponse(
    @SerializedName("list") val forecastday: List<ForecastDay>
) {
    data class ForecastDay(
        @SerializedName("dt_txt") val date: String,
        @SerializedName("main") val day: Day,
        @SerializedName("weather") val condition: List<Condition>
    ) {
        data class Day(
            @SerializedName("temp_max") val maxtempC: Float,
            @SerializedName("temp_min") val mintempC: Float
        )

        data class Condition(
            @SerializedName("icon") val icon: String
        )
    }
}
