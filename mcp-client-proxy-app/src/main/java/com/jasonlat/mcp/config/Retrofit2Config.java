package com.jasonlat.mcp.config;

import com.jasonlat.mcp.api.IOpenAiApiProxy;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Configuration
public class Retrofit2Config {

    @Bean
    public IOpenAiApiProxy openAiApi(
            @Value("${spring.ai.agent.base-url}") String baseUrl,
            @Value("${spring.ai.agent.api-key}") String apiKey,

            @Value("${spring.ai.proxy.host:127.0.0.1}") String proxyHost,
            @Value("${spring.ai.proxy.port:7890}") int proxyPort,

            // 超时配置（可写配置文件，也可以直接写死）
            @Value("${spring.ai.timeout.connect:300}") long connectTimeout,
            @Value("${spring.ai.timeout.read:300}") long readTimeout,
            @Value("${spring.ai.timeout.write:300}") long writeTimeout
    ) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

//        // 1. 设置代理
//        okHttpClientBuilder.proxy(new Proxy(Proxy.Type.HTTP,
//                new InetSocketAddress(proxyHost, proxyPort)));

        // 2. ====================== 【关键：设置超时时间】 ======================
        okHttpClientBuilder
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)   // 连接超时
                .readTimeout(readTimeout, TimeUnit.SECONDS)         // 读取超时（大模型必须长）
                .writeTimeout(writeTimeout, TimeUnit.SECONDS);      // 写入超时
        // ====================================================================

        OkHttpClient okHttpClient = okHttpClientBuilder
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + apiKey)
                            .build();
                    return chain.proceed(request);
                })
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(IOpenAiApiProxy.class);
    }

}
