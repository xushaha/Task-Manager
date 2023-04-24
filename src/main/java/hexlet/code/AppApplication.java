package hexlet.code;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.rollbar.notifier.Rollbar;
import static com.rollbar.spring.webmvc.RollbarSpringConfigBuilder.withAccessToken;

@SecurityScheme(name = "JWT", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SpringBootApplication
public class AppApplication {

    // для тестирования Rollbar
    private static Rollbar rollbar = Rollbar.init(withAccessToken("57e709e076a343619b4c631ddbc47ae5")
            .environment("qa")
            .codeVersion("1.0.0")
            .build());


    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
        rollbar.debug("Hello, Rollbar");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

/*    public static void rollbarStart() throws Exception {
        Rollbar rollbar = Rollbar.init(withAccessToken("57e709e076a343619b4c631ddbc47ae5")
                .environment("qa")
                .codeVersion("1.0.0")
                .build());

        rollbar.log("Hello, Rollbar");
        rollbar.close(true);
    }*/

}


