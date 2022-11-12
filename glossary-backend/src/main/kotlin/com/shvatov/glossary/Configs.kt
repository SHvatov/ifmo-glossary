package com.shvatov.glossary

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.newSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, GlossaryItem> {
        val context = newSerializationContext<String, GlossaryItem>()
            .key(StringRedisSerializer())
            .hashKey(StringRedisSerializer())
            .hashValue(Jackson2JsonRedisSerializer(GlossaryItem::class.java))
            .value(Jackson2JsonRedisSerializer(GlossaryItem::class.java))
            .build()
        return ReactiveRedisTemplate(factory, context)
    }
}