package com.cd;

import com.cd.model.Todo;
import com.cd.repository.TodoRepository;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class TodoH2Application implements CommandLineRunner {

    @Autowired
    private TodoRepository todoRepository;

    public static void main(String[] args) {
        SpringApplication.run(TodoH2Application.class, args);
    }

    @Override
    public void run(String... args) {
        Todo todo = new Todo();
        todo.setDone(false);
        todo.setUser("Jack");
        todo.setTask("Learn Spring Boot!");
        todo.setTargetDate(LocalDate.of(2024, 12, 29));
        todoRepository.save(todo);
    }

    @Bean
    public OpenAPI springTodoOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Todo API")
                        .description("Todo h2 application")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Todo Wiki Documentation")
                        .url("https://Todo.wiki.github.org/docs"));
    }

}
