package ru.matrix.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.Nullable;

@Configuration
public class AppConfig {
    private static final int THREAD_POOL_SIZE = 2;

    @Bean
    public WebClient datastoreClient(WebClient.Builder builder, @Value("${calculator.url}") String url) {
        return builder
                .baseUrl(url)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ReactiveWebServerFactory reactiveWebServerFactory() {
        var eventLoopGroup = new NioEventLoopGroup(THREAD_POOL_SIZE,
                new ThreadFactory() {
                    private final AtomicLong threadIdGenerator = new AtomicLong(0);
                    @Override
                    public Thread newThread(@NonNull Runnable task) {
                        return new Thread(task, "server-thread-" + threadIdGenerator.incrementAndGet());
                    }
                });

        var factory = new NettyReactiveWebServerFactory();
        factory.addServerCustomizers(builder -> builder.runOn(eventLoopGroup));

        return factory;
    }

    @Bean
    public ReactorResourceFactory reactorResourceFactory() {
        var eventLoopGroup = new NioEventLoopGroup(THREAD_POOL_SIZE,
                new ThreadFactory() {
                    private final AtomicLong threadIdGenerator = new AtomicLong(0);
                    @Override
                    public Thread newThread(@Nullable Runnable task) {
                        var thread = new Thread(task);
                        thread.setName("client-thread-" + threadIdGenerator.incrementAndGet());
                        return thread;
                    }
                });

        var resourceFactory= new ReactorResourceFactory();
        resourceFactory.setLoopResources(b -> eventLoopGroup);
        resourceFactory.setUseGlobalResources(false);
        return resourceFactory;
    }

    @Bean
    public ReactorClientHttpConnector reactorClientHttpConnector(ReactorResourceFactory resourceFactory) {
        return new ReactorClientHttpConnector(resourceFactory, mapper -> mapper);
    }

    @Bean
    public Scheduler workerPool() {
        return Schedulers.newParallel("processor-thread", THREAD_POOL_SIZE);
    }
}
