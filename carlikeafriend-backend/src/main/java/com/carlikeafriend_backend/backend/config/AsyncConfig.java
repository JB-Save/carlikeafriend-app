package com.carlikeafriend_backend.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

// Esta clase habilita el procesamiento @Async en toda la aplicación
@Configuration
@EnableAsync
public class AsyncConfig {

    // Configuración explícita del Executor Asíncrono para mejor monitoreo y control de rendimiento.

    // 1. Executor para Emails (Alta prioridad, menor tolerancia a fallos)
    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Define un número de hilos base
        executor.setMaxPoolSize(5);  // Define el máximo de hilos (para picos de carga)
        executor.setQueueCapacity(100); // Define la cola de espera para tareas pendientes
        executor.setThreadNamePrefix("Email-Async-");  // Nombre del Thread: Crucial para el monitoreo
        executor.initialize();
        return executor;
    }

    // 2. Executor para Métricas y Redes Sociales (Baja prioridad, alta concurrencia)
    @Bean(name = "shareInteractionExecutor")
    public Executor shareInteractionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(10); // Permitimos más hilos si hay picos
        executor.setQueueCapacity(500); // Cola más grande ya que no bloquea al usuario
        executor.setThreadNamePrefix("Share-Async-");
        executor.initialize();
        return executor;
    }

}
