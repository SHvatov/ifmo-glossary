package com.shvatov.glossary

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono

@Controller
class GlossaryController(private val repository: GlossaryItemRepository) {

    enum class SortOrder {
        ASC, DESC
    }

    @QueryMapping("getGlossaryItems")
    fun getGlossaryItems(
        @Argument limit: Long?,
        @Argument offset: Long?,
        @Argument sort: SortOrder?,
        @Argument nameFilter: String?
    ) = repository.findAll()
        .filter { item ->
            nameFilter?.let { substring ->
                item.name
                    ?.lowercase()
                    ?.contains(substring.lowercase())
                    ?: false
            } ?: true
        }
        .let { flux ->
            if (sort != null) {
                flux.sort(
                    Comparator.comparing<GlossaryItem, String> { it.name!! }
                        .let {
                            if (sort == SortOrder.DESC) {
                                it.reversed()
                            } else it
                        }
                )
            } else flux
        }
        .let {
            if (offset != null) {
                it.skip(offset)
            } else it
        }
        .let {
            if (limit != null) {
                it.take(limit)
            } else it
        }

    @MutationMapping
    fun upsertGlossaryItem(@Argument item: GlossaryItem) =
        repository.save(Mono.just(item))
}