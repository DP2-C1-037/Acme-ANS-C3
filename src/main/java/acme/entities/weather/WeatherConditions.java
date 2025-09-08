
package acme.entities.weather;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidNumber;
import acme.client.components.validation.ValidString;
import acme.entities.airports.Airport;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class WeatherConditions extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Valid
	@ManyToOne
	private Airport				airport;

	@Mandatory
	@ValidNumber
	@Automapped
	private Double				temperature;   // Temperatura en Kelvin (directo de la API)

	@Mandatory
	@ValidNumber
	@Automapped
	private Double				windSpeed;     // Velocidad del viento en m/s (directo de la API)

	@Mandatory
	@ValidString
	@Automapped
	private String				conditions;    // Ej: "clear sky", "rain"

	@Mandatory
	@ValidNumber
	@Automapped
	private Double				humidity;     // Porcentaje (0-100)

	@Mandatory
	@ValidMoment
	@Temporal(TemporalType.TIMESTAMP)
	private Date				dataDate;

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
