package com.github.springbootkotlingithubapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "github")
open class GithubProperties {
    /*
    *  A token to use Github Public API.
    * */
    var token: String? = null
    var rootUri: String? = null
}