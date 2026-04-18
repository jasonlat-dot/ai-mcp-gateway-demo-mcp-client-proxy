package com.jasonlat.mcp.test;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.AsyncMcpToolCallback;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

/**
 * @author jasonlat
 * 2026-04-18  16:55
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Test
    public void test() {
        ChatClient chatClient = chatClientBuilder.defaultOptions(
                OpenAiChatOptions.builder()
                        .model("gpt-5.4")
                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient()).getToolCallbacks()).build())
                .build();
        log.info("测试结果：{}", chatClient.prompt("有哪些工具可以使用？").call().content());
    }

    public McpSyncClient sseMcpClient() {
        HttpClientSseClientTransport sseClientTransport =
                HttpClientSseClientTransport.builder(
                        "http://localhost:8701/sse"
                ).build();
        McpSyncClient sseMcpClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofDays(30000)).build();
        var initialize = sseMcpClient.initialize();
        log.info("sseMcpClient: {}", initialize);
        return sseMcpClient;
    }
}
