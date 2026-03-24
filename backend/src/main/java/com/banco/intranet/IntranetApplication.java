package com.banco.intranet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Aplicación principal de la Intranet Bancaria
 * 
 * @author Arquitectura Enterprise
 * @version 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.banco.intranet")
public class IntranetApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntranetApplication.class, args);
	}

}
