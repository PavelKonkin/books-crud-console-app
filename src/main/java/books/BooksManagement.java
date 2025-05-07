package books;

import books.app.BookManagementApp;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ComponentScan(basePackages = "books")
public class BooksManagement {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(BooksManagement.class);
        BookManagementApp app = context.getBean(BookManagementApp.class);
        app.run();
    }
}
