package com.github.springbootkotlingithubapi

import com.fasterxml.jackson.annotation.JsonProperty

data class Actor(@JsonProperty("login") val login: String,
                 @JsonProperty("avatar_url") val avatarUrl: String,
                 @JsonProperty("html_url") val htmlUrl: String)