package com.github.springbootkotlingithubapi.controller

import com.github.springbootkotlingithubapi.client.GithubClient
import com.github.springbootkotlingithubapi.model.DashboardEntry
import com.github.springbootkotlingithubapi.model.RepositoryEvent
import com.github.springbootkotlingithubapi.repository.GithubProjectRepository
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import java.util.stream.StreamSupport
import kotlin.collections.set
import kotlin.streams.toList


@Controller
class GithubIssuesController(private val githubClient: GithubClient,
                             private val githubProjectRepository: GithubProjectRepository) {

    @GetMapping("/issues/{repoName}")
    @ResponseBody
    fun findIssueEventsByOrgNameAndRepoName(@PathVariable repoName: String, pageable: Pageable?): ResponseEntity<List<RepositoryEvent>> {
        val githubProject = githubProjectRepository.findByRepoName(repoName)

        return if (githubProject == null) {
            ResponseEntity
                    .notFound()
                    .build()
        } else {
            val responseEntity = githubClient.fetchEvents(githubProject.orgName, githubProject.repoName, pageable)

            val httpHeaders = HttpHeaders()

            val responseHeadersFromGithubAPI = responseEntity.headers

            httpHeaders["X-RateLimit-Limit"] = responseHeadersFromGithubAPI["X-RateLimit-Limit"]
            httpHeaders["X-RateLimit-Remaining"] = responseHeadersFromGithubAPI["X-RateLimit-Remaining"]
            httpHeaders["X-RateLimit-Reset"] = responseHeadersFromGithubAPI["X-RateLimit-Reset"]

            ResponseEntity
                    .ok()
                    .headers(httpHeaders)
                    .body(responseEntity.body)
        }
    }

    @RequestMapping(value = ["/", "/issues/dashboard"])
    fun goTodashboardView(model: Model): ModelAndView {
        val entries = StreamSupport.stream(this.githubProjectRepository.findAll().spliterator(), true)
                .map { DashboardEntry(it, githubClient.fetchEvents(orgName = it.orgName, repoName = it.repoName).body) }
                .toList()

        model["entries"] = entries

        return ModelAndView(
                RedirectView("/dashboard", true),
                model.asMap()
        )

    }
}
