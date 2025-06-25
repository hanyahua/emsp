package com.volvo.emsp;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

@TestConfiguration
public class TestRedisConfiguration {

    private RedisServer redisServer;

    private int redisPort;

    @Bean
    public RedisServer redisServer(@Value("${spring.data.redis.port:6379}") int defaultPort) throws IOException {
        this.redisPort = findAvailablePort(defaultPort);
        this.redisServer = new RedisServer(redisPort);
        redisServer.start();
        System.out.println("Embedded Redis started on port: " + redisPort);
        return this.redisServer;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName("localhost");
        redisConfig.setPort(redisPort); //
        return new LettuceConnectionFactory(redisConfig);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    @PreDestroy
    public void stopRedis() {
        try {
            if (redisServer != null && redisServer.isActive()) {
                redisServer.stop();
                System.out.println("Embedded Redis stopped");
            }
        } catch (Exception e) {
            System.err.println("Error during Redis shutdown: " + e.getMessage());
        }
    }

    /**
     * Finds a random available port to avoid conflicts in parallel tests
     */
    private int findAvailablePort(int defaultPort) {
        try (ServerSocket socket = new ServerSocket()) {
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress("localhost", 0)); // Bind to an ephemeral port
            return socket.getLocalPort();
        } catch (IOException e) {
            return defaultPort; // Fallback to the default port
        }
    }

}
