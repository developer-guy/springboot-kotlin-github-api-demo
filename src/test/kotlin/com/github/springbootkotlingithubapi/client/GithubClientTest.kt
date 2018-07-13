package com.github.springbootkotlingithubapi.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.springbootkotlingithubapi.configuration.GithubProperties
import com.github.springbootkotlingithubapi.configuration.RestTemplateConfiguration
import com.github.springbootkotlingithubapi.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import java.time.OffsetDateTime


@RunWith(SpringRunner::class)
@SpringBootTest
@RestClientTest(GithubClient::class)
@Import(RestTemplateConfiguration::class)
class GithubClientTest {

    @Autowired
    lateinit var githubClient: GithubClient

    @Autowired
    lateinit var mockRestServiceServer: MockRestServiceServer

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var githubProperties: GithubProperties

    @Test
    fun `This client should fetch issues from Github API 😎`() {
        //ARRANGE
        val repositoryEvent = RepositoryEvent(
                Type.ASSIGNED,
                OffsetDateTime.now(),
                Actor("test_actor_login.url", "test_actor_avatar.url", "test_actor_html.url"),
                Issue("test_issue_html.url", 0, "test_title")
        )

        val repositoryEvents = RepositoryEvents().initializeWith(repositoryEvent)

        val bodyAsJSON = objectMapper.writeValueAsString(repositoryEvents)

        mockRestServiceServer.expect(MockRestRequestMatchers
                .requestTo("/repos/spring-projects/spring-data-jpa/issues/events?page=0&per_page=20"))
                .andRespond(MockRestResponseCreators.withSuccess(bodyAsJSON, MediaType.APPLICATION_JSON_UTF8))

        //ACT
        val response: ResponseEntity<RepositoryEvents> = githubClient.fetchEvents("spring-projects",
                "spring-data-jpa",
                PageRequest.of(0, 20, Sort.Direction.ASC, "id"))

        //ASSERT
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        assertThat(response.body?.size).isEqualTo(1)
        assertThat(response.body!![0].type).isEqualTo(Type.ASSIGNED)
    }

}

fun RepositoryEvents.initializeWith(vararg others: RepositoryEvent): RepositoryEvents {
    others.forEach { this.add(it) }
    return this
}