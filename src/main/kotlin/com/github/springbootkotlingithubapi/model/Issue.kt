package com.github.springbootkotlingithubapi.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Issue(@JsonProperty("html_url") val htmlUrl: String,
                 @JsonProperty("number") val number: Int,
                 @JsonProperty("title") val title: String)