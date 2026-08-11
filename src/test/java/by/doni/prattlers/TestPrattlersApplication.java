package by.doni.prattlers;

import org.springframework.boot.SpringApplication;

public class TestPrattlersApplication {

    public static void main(String[] args) {
        SpringApplication.from(PrattlersApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
