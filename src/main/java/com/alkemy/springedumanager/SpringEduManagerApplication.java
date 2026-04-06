package com.alkemy.springedumanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// ╔═══════════════════════════════════════════════════════════╗
// ║  Punto de entrada del proyecto                            ║
// ║  @SpringBootApplication activa:                           ║
// ║    · Autoconfiguración de Tomcat, JPA, Security, etc.     ║
// ║    · Escaneo de @Component, @Service, @Repository         ║
// ║      en todos los subpaquetes de com.alkemy.springedu     ║
// ╚═══════════════════════════════════════════════════════════╝
@SpringBootApplication
public class SpringEduManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringEduManagerApplication.class, args);
    }
}
