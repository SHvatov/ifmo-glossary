package com.shvatov.glossary

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.io.File
import javax.annotation.PostConstruct

@EnableWebFlux
@SpringBootApplication
class GlossaryApplication(private val repository: GlossaryItemRepository) {

    @Value("\${glossary.path-to-init-data}")
    private lateinit var pathToInitData: String

    @Value("\${glossary.init-data-separator}")
    private lateinit var initDataSeparator: String

    @PostConstruct
    fun initMissingData() {
        repository.findAll()
            .continueIfEmpty()
            .flatMapIterable { readInitData() }
            .map { mapToGlossaryItem(it) }
            .flatMap { item ->
                repository.save(Mono.just(item))
            }.blockLast()
    }

    private fun <T> Flux<T>.continueIfEmpty(): Mono<Unit> =
        count()
            .filter { 0L == it }
            .flatMap { Unit.toMono() }

    private fun readInitData() = runCatching {
        csvReader {
            delimiter = initDataSeparator[0]
        }.open(File(pathToInitData)) {
            readAllWithHeaderAsSequence()
                .toList()
        }
    }.onFailure {
        LOGGER.error(it) { "Failed to read CSV file \"$pathToInitData\"" }
    }.getOrElse { emptyList() }

    private fun mapToGlossaryItem(record: Map<String, String?>) =
        GlossaryItem(record["name"], record["description"], record["link"])

    private companion object {
        val LOGGER = KotlinLogging.logger {}
    }
}

fun main(args: Array<String>) {
    runApplication<GlossaryApplication>(*args)
}
