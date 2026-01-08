package com.carlikeafriend_backend.backend.email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

// Esta clase habilita el procesamiento @Async en toda la aplicación
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    // Configuración explícita del Executor Asíncrono para mejor
    //monitoreo y control de rendimiento.
    // Define un número de hilos base
    private static final int CORE_POOL_SIZE = 2;
    // Define el máximo de hilos (para picos de carga)
    private static final int MAX_POOL_SIZE = 5;
    // Define la cola de espera para tareas pendientes
    private static final int QUEUE_CAPACITY = 100;

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        // Nombre del Thread: Crucial para el monitoreo
        executor.setThreadNamePrefix("Email-Async-");
        executor.initialize();
        return executor;
    }
}
