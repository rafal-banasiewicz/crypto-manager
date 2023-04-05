package pl.rb.manager.exchange.utils;

import com.squareup.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfig {

    @Bean
    public static OkHttpClient getHttpClient() {
        return new OkHttpClient();
    }

}
