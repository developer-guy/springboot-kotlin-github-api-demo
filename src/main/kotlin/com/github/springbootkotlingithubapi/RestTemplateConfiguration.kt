package com.github.springbootkotlingithubapi

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.util.StringUtils
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfiguration {

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder,
                     properties: GithubProperties): RestTemplate = restTemplateBuilder
            .rootUri(properties.rootUri)
            .interceptors(TokenCheckInterceptor(properties.token))
            .build()


    class TokenCheckInterceptor(private val token: String? = null) : ClientHttpRequestInterceptor {

        override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
            if (StringUtils.hasText(token)) {
                request.headers.add(HttpHeaders.AUTHORIZATION, "token $token")
            }

            return execution.execute(request, body)
        }

    }

}