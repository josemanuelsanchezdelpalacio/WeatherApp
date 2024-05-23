package com.example.weatherapp.data

/**para representar la informacion del pronostico segun el dia**/
data class ForecastResponse(
    val forecastday: List<ForecastDay>
) {
    data class ForecastDay(
        val date: String,
        val day: Day,
        val hour: List<Hour>
    ) {
        data class Day(
            val maxtempC: Double,
            val mintempC: Double,
            val avgtempC: Double,
            val condition: Condition
        ) {
            data class Condition(
                val text: String,
                val icon: String
            )
        }

        data class Hour(
            val time: String,
            val tempC: Double,
            val condition: Condition
        ) {
            data class Condition(
                val text: String,
                val icon: String
            )
        }
    }
}
