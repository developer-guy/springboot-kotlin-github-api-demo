package com.github.springbootkotlingithubapi.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import java.time.OffsetDateTime

//@JsonCreator is optional unless secondary constructor available!!
data class RepositoryEvent(@JsonProperty("event") @JsonDeserialize(using = EventToTypeDeSerializer::class) val type: Type,
                           @JsonProperty("created_at") val creationTime: OffsetDateTime,
                           @JsonProperty("actor") val actor: Actor,
                           @JsonProperty("issue") val issue: Issue)