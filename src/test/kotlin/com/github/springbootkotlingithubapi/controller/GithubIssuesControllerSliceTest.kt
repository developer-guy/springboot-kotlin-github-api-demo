package com.github.springbootkotlingithubapi.controller

import com.github.springbootkotlingithubapi.client.GithubClient
import com.github.springbootkotlingithubapi.client.addOthers
import com.github.springbootkotlingithubapi.domain.GithubProject
import com.github.springbootkotlingithubapi.model.*
import com.github.springbootkotlingithubapi.repository.GithubProjectRepository
import org.hamcrest.core.Is
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.web.config.EnableSpringDataWebSupport
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.OffsetDateTime


@RunWith(SpringRunner::class)
@WebMvcTest(GithubIssuesController::class)
@EnableSpringDataWebSupport
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GithubIssuesControllerSliceTest {

    @Autowired
    private lateinit var mockmvc: MockMvc

    @MockBean
    private lateinit var githubProjectRepository: GithubProjectRepository

    @MockBean
    private lateinit var githubClient: GithubClient

    @Test
    fun `It Should Find Issue Events By Org Name and Repo Name`() {
        // Arrange
        val pageable = PageRequest.of(0, 20, Sort.unsorted())
        given(githubProjectRepository.findByRepoName("spring-boot")).willReturn(GithubProject("spring-boot", "spring", 1L))

        val httpHeaders = HttpHeaders()
        httpHeaders["X-RateLimit-Limit"] = "100"
        httpHeaders["X-RateLimit-Remaining"] = "50"
        httpHeaders["X-RateLimit-Reset"] = "0"

        val repositoryEvent = RepositoryEvent(
                Type.ASSIGNED,
                OffsetDateTime.now(),
                Actor("test_actor_login.url", "test_actor_avatar.url", "test_actor_html.url"),
                Issue("test_issue_html.url", 0, "test_title")
        )

        val repositoryEvents = RepositoryEvents().addOthers(repositoryEvent)

        given(githubClient.fetchEvents("spring", "spring-boot", pageable)).willReturn(ResponseEntity.ok().headers(httpHeaders).body(repositoryEvents))

        // Act
        val resultActions = mockmvc.perform(get("/issues/{repoName}", "spring-boot"))

        // Assert
        resultActions.andExpect(status().is2xxSuccessful)
        resultActions.andExpect(header().exists("X-RateLimit-Limit"))
        resultActions.andExpect(header().exists("X-RateLimit-Remaining"))
        resultActions.andExpect(header().exists("X-RateLimit-Reset"))
        resultActions.andExpect(jsonPath("$[0].event", Is.`is`("ASSIGNED")))
    }

    @Test
    fun `It Should Redirect to Dashboard Page`() {
        // Arrange
        given(githubProjectRepository.findAll()).willReturn(listOf<GithubProject>())

        // Act
        val resultActions = mockmvc.perform(get("/"))

        // Assert
        resultActions.andExpect(status().is3xxRedirection)
        resultActions.andExpect(redirectedUrl("/dashboard"))
    }
}