package com.example.expense.ai.client;

import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.AgentEventType;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.model.Model;
import io.agentscope.harness.agent.HarnessAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlmClient {

    private final Model model;

    private static final Path WORKSPACE = Path.of(System.getProperty("java.io.tmpdir"), "expense-agent-workspace");

    public String chat(String systemPrompt, String userMessage) {
        HarnessAgent agent = HarnessAgent.builder()
                .name("temp-" + UUID.randomUUID().toString().substring(0, 8))
                .sysPrompt(systemPrompt)
                .model(model)
                .workspace(WORKSPACE)
                .disableSubagents()
                .disableFilesystemTools()
                .disableShellTool()
                .build();

        Msg result = agent.call(new UserMessage(userMessage), RuntimeContext.empty()).block();
        if (result == null) {
            throw new RuntimeException("Agent returned null response");
        }
        String text = result.getTextContent();
        if (text == null || text.isBlank()) {
            // fallback: try extracting from content blocks
            text = result.getContent().stream()
                    .filter(b -> b instanceof io.agentscope.core.message.TextBlock)
                    .map(b -> ((io.agentscope.core.message.TextBlock) b).getText())
                    .findFirst().orElse("");
        }
        log.debug("LLM response: {}", text);
        if (text.isBlank()) {
            throw new RuntimeException("Empty response from LLM");
        }
        return text;
    }

    public void chatStream(String systemPrompt, String userMessage, Consumer<String> onChunk) {
        HarnessAgent agent = HarnessAgent.builder()
                .name("temp-" + UUID.randomUUID().toString().substring(0, 8))
                .sysPrompt(systemPrompt)
                .model(model)
                .workspace(WORKSPACE)
                .disableSubagents()
                .disableFilesystemTools()
                .disableShellTool()
                .build();

        agent.streamEvents(new UserMessage(userMessage), RuntimeContext.empty())
                .doOnNext(event -> {
                    if (event.getType() == AgentEventType.TEXT_BLOCK_DELTA
                            && event instanceof TextBlockDeltaEvent deltaEvent) {
                        String text = deltaEvent.getDelta();
                        if (text != null && !text.isEmpty()) {
                            onChunk.accept(text);
                        }
                    }
                })
                .blockLast();
    }
}
