package books;

import books.app.BookManagementApp;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = false)
public class BooksManagement {
    public static void main(String[] args) {
        SpringApplication.run(BooksManagement.class, args);
    }

    // Метод для запуска консольной логики после старта контекста
    @Bean
    public CommandLineRunner runConsoleApp(BookManagementApp app) {
        return args -> {
            app.run();  // Вызов логики консольного приложения
        };
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        return messageSource;
    }
}
