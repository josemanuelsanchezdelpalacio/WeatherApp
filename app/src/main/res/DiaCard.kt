@Composable
fun DiaCard(pronostico: ForecastResponse.ForecastDay?) {
    pronostico?.let { forecastDay ->
        Card(
            modifier = Modifier.padding(4.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = forecastDay.date ?: "",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                forecastDay.day?.let { day ->
                    Image(
                        painter = rememberImagePainter(data = day.condition?.icon ?: ""),
                        contentDescription = "Icono del clima",
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Max: ${day.maxtempC}ºC",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Min: ${day.mintempC}ºC",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
