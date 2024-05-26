package com.example.weatherapp.data

/**para representar la informacion del pronostico segun el dia**/
data class ForecastResponse(
    val forecastday: List<ForecastDay>
) {
    data class ForecastDay(
        val date: String,
        val day: Day
    ) {
        data class Day(
            val maxtempC: Float,
            val mintempC: Float,
            val condition: Condition
        ) {
            data class Condition(
                val icon: String
            )
        }
    }
}
