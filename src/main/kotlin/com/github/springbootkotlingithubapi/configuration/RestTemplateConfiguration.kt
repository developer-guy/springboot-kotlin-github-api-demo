package com.github.springbootkotlingithubapi.configuration

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestTemplate


@Configuration
@EnableConfigurationProperties(GithubProperties::class)
class RestTemplateConfiguration {

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder,
                     properties: GithubProperties): RestTemplate = restTemplateBuilder
            .rootUri(properties.rootUri)
            .interceptors(TokenCheckInterceptor(properties.token))
            .build()


    class TokenCheckInterceptor constructor(private val token: String?) : ClientHttpRequestInterceptor {

        override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
            if (token.hasText()) {
                request.addToken2RequestHeaders(token)
            }
            return execution.execute(request, body)
        }

    }

}

fun HttpRequest.addToken2RequestHeaders(token: String?) {
    this.headers.add(HttpHeaders.AUTHORIZATION, "token $token")
}

//extension function
fun String?.hasText(): Boolean = this != null && this.isNotEmpty() && this.isNotBlank()