package com.example.codemind.app;

import com.plexpt.chatgpt.ChatGPT;
import com.plexpt.chatgpt.util.Proxys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.Proxy;

/**
 * @Author ouyangkang
 * @date 2023/8/14
 */
@Configuration
public class GPTConfig {


    @Value("${openai.apiKey:}")
    private String apiKey;

    @Bean
    public ChatGPT getChatGPT() {
        Proxy proxy = Proxys.http("127.0.0.1", 7890);
        //socks5 代理
        // Proxy proxy = Proxys.socks5("127.0.0.1", 1080);
//
        ChatGPT chatGPT = ChatGPT.builder()
                .apiKey(apiKey)
                .proxy(proxy)
//                .apiHost("https://api.openai.com/") //反向代理地址
                .build()
                .init();
        return chatGPT;
    }
}
