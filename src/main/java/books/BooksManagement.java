package books;

import books.app.BookManagementApp;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.io.IOException;
import java.util.Locale;

@Configuration
@ComponentScan(basePackages = "books")
@EnableAspectJAutoProxy(proxyTargetClass = false)
public class BooksManagement {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(BooksManagement.class);
        BookManagementApp app = context.getBean(BookManagementApp.class);
        app.run();
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
