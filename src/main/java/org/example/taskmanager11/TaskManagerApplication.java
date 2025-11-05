package org.example.taskmanager11;

import org.example.taskmanager11.repo.ClientRepository;
import org.example.taskmanager11.services.ClientService;
import org.example.taskmanager11.services.TaskService;
import org.example.taskmanager11.utils.Utils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// E -> R -> S -> C

@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(TaskService taskService, ClientService clientService, ClientRepository clientRepository) {
        return args -> {
            System.out.println("Hello World");

            if (clientRepository.findByLogin("111") == null) {
                System.out.println("Adding sample data...");

                for (int i = 0; i < 10; i++) {
                    taskService.addTask("Test task #" + i);
                }

                String salt = Utils.generateRandomString(10);
                clientService.addClient("111", salt, Utils.passwordHash(salt, "222"));
            } else {
                System.out.println("Sample data already exists.");
            }
        };
    }
}
