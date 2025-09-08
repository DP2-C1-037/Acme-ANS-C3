
package acme.entities.weather;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import acme.client.services.AbstractService;
import acme.entities.airports.Airport;

@SuppressWarnings("rawtypes")
@Service
public class WeatherService extends AbstractService {

	private static final String	API_KEY			= "478a54206224dd3e0153ae2c8570045d";
	private static final String	URL_TEMPLATE	= "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s";


	public WeatherConditions getWeatherForAirport(final Airport airport) {
		try {
			// Codificar ciudades para URL
			String encodedCity = URLEncoder.encode(airport.getCity(), StandardCharsets.UTF_8);

			// Consultar datos meteorológicos para ambas ciudades
			WeatherData departureWeather = this.fetchWeatherData(encodedCity);

			// Crear objeto WeatherConditions con datos combinados
			WeatherConditions weatherConditions = new WeatherConditions();
			weatherConditions.setAirport(airport);
			// Datos del aeropuerto de salida
			weatherConditions.setTemperature(departureWeather.getMain().getTemp());
			weatherConditions.setWindSpeed(departureWeather.getWind().getSpeed());
			weatherConditions.setConditions(departureWeather.getWeather().get(0).getDescription());
			weatherConditions.setHumidity(departureWeather.getMain().getHumidity());
			weatherConditions.setDataDate(new Date());

			return weatherConditions;
		} catch (Exception e) {
			throw new RuntimeException("Error fetching weather data", e);
		}
	}

	// Método auxiliar para obtener datos meteorológicos
	@SuppressWarnings("deprecation")
	private WeatherData fetchWeatherData(final String city) throws IOException {
		String urlString = String.format(WeatherService.URL_TEMPLATE, city, WeatherService.API_KEY);
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		// Leer respuesta con Jackson
		ObjectMapper mapper = new ObjectMapper();
		try (InputStream inputStream = conn.getInputStream()) {
			return mapper.readValue(inputStream, WeatherData.class);
		}
	}
}
