package com.shvatov.glossary

import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class GlossaryItemRepository(private val redisTemplate: ReactiveRedisTemplate<String, GlossaryItem>) {

    fun save(item: Mono<GlossaryItem>): Mono<GlossaryItem> {
        return with(redisTemplate) {
            with(opsForHash<String, GlossaryItem>()) {
                item.flatMap {
                    put(GLOSSARY_HASH, it.name!!, it)
                }.flatMap { success ->
                    if (success) {
                        item
                    } else Mono.empty()
                }
            }
        }
    }

    fun findAll(): Flux<GlossaryItem> {
        return with(redisTemplate) {
            with(opsForHash<String, GlossaryItem>()) {
                scan(GLOSSARY_HASH).map {
                    GlossaryItem(it.key, it.value.description, it.value.link)
                }
            }
        }
    }

}