package racingcar.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String API_TITLE = "자동자 경주";
    private static final String API_DESCRIPTION = "자동차 경주 API 문서";
    private static final String API_VERSION = "1.0.0";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(getInfo());
    }

    private Info getInfo() {
        return new Info()
                .title(API_TITLE)
                .description(API_DESCRIPTION)
                .version(API_VERSION);
    }
}
