import com.books.gateway.GatewayApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = GatewayApplication.class)
public class MessageSourceTest {

    @Autowired
    private MessageSource messageSource;

    @Test
    public void testMessageSource() {
        String message = messageSource.getMessage("imageUploadError", null, new Locale("ru"));
        assertThat(message).isNotNull();
    }
}
