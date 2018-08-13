/*
We’ll be using Java 8 Data/Time classes in our domain models. 
We’ll need to register JPA 2.1 converters so that all the Java 8 Date/Time fields 
in the domain models automatically get converted to SQL types
when we persist them in the database.

Moreover, We’ll set the default timezone for our application to UTC.
 */

package com.example.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackageClasses = {
		ProjectApplication.class,
		Jsr310JpaConverters.class
})
public class ProjectApplication {

	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
	}
}
