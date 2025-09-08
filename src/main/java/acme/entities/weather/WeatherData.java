
package acme.entities.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {

	private Main			main;
	private Wind			wind;
	private List<Weather>	weather;
	private String			name;


	@Getter
	@Setter
	public static class Main {

		private Double	temp;       // Temperatura en Kelvin
		private Double	humidity;   // Humedad en %
		private Double	pressure;   // Presión atmosférica en hPa
	}

	@Getter
	@Setter
	public static class Wind {

		private Double	speed;      // Velocidad del viento en m/s
		private Double	deg;        // Dirección del viento en grados
	}

	@Getter
	@Setter
	public static class Weather {

		private String	main;       // Clima general (ej: "Rain", "Clear")
		private String	description;// Descripción detallada
		private String	icon;       // Código del ícono (ej: "01d")
	}

	// EJEMPLO RESPUESTA API OpenWeatherMap
	//	{
	//		  "weather": [{"main": "Clear", "description": "clear sky"}],
	//		  "main": {
	//		    "temp": 298.15, // Kelvin (25°C)
	//		    "humidity": 65
	//		  },
	//		  "wind": {
	//		    "speed": 3.6 // m/s (12.96 km/h)
	//		  },
	//		  "name": "Madrid"
	//		}
}
