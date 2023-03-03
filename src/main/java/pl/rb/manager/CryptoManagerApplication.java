package pl.rb.manager;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class CryptoManagerApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(CryptoManagerApplication.class, args);
//        OkHttpClient client = new OkHttpClient();
//        String URL = "http://localhost:8082/getInstruments";
//        Request request = new Request.Builder().url(URL).get().build();
//        String response = client.newCall(request).execute().body().string();
//        System.out.println(response);
    }

}
