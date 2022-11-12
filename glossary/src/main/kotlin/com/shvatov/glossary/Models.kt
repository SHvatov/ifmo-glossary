package com.shvatov.glossary

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

const val GLOSSARY_HASH = "glossary";

@RedisHash(value = GLOSSARY_HASH)
data class GlossaryItem(
    @field:JsonIgnore
    @field:Id val name: String? = null,
    val description: String? = null,
    val link: String? = null
) : Serializable