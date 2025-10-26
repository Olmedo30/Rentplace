package es.uclm.rentplace;

import java.sql.DriverManager;
import jakarta.annotation.PreDestroy;     
import java.sql.SQLException; 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@SpringBootApplication
public class RentplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentplaceApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordencoder() {
		return new BCryptPasswordEncoder();
	}
	@PreDestroy
	public void shutdownDerby() {
	    try {
	        DriverManager.getConnection("jdbc:derby:;shutdown=true");
	    } catch (SQLException e) {
	        // Derby lanza una excepción incluso en shutdown exitoso
	        if (!e.getSQLState().equals("08006")) {
	            e.printStackTrace();
	        }
	    }
	}
}
