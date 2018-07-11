package com.github.springbootkotlingithubapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "github")
class GithubProperties {
    /*
    *  A token to use Github Public API.
    * */
    var token: String = ""
    var rootUri: String? = null
}