package com.lucas.taskboard;

import com.lucas.taskboard.menu.MainMenu;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskBoardApplication implements CommandLineRunner {

    private final MainMenu mainMenu;

    public TaskBoardApplication(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public static void main(String[] args) {
        SpringApplication.run(TaskBoardApplication.class, args);
    }

    @Override
    public void run(String... args) {
        mainMenu.exibirMenu();
    }
}
