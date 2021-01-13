package io.bytestreme.sshbot.handler;

import io.bytestreme.sshbot.handler.annotation.BotHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class HandlerConfiguration {

    @Bean
    public Map<String, AbstractHandler> handlerMap(@Autowired List<AbstractHandler> handlers) {
        return handlers.stream()
                .collect(Collectors.toMap(x -> x.getClass()
                                .getAnnotation(BotHandler.class)
                                .command(),
                        x -> x)
                );
    }

}
