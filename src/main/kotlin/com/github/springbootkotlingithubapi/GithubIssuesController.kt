package com.github.springbootkotlingithubapi

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
import java.util.stream.StreamSupport
import kotlin.streams.toList


@Controller
@RequestMapping("/issues")
class GithubIssuesController(private val githubClient: GithubClient,
                             private val githubProjectRepository: GithubProjectRepository) {

    @GetMapping("{orgName}/{repoName}/events")
    @ResponseBody
    fun findIssueEventsByOrgNameAndRepoName(@PathVariable orgName: String, @PathVariable repoName: String, pageable: Pageable): ResponseEntity<RepositoryEvents> {
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
                    .headers(responseHeadersFromGithubAPI)
                    .body(responseEntity.body)
        }
    }

    @GetMapping
    fun dashboardView(model: Model): String {
        val entries = StreamSupport.stream(this.githubProjectRepository.findAll().spliterator(), true)
                .map { DashboardEntry(it, githubClient.fetchEvents(orgName = it.orgName, repoName = it.repoName).body) }.toList()

        model["entries"] = entries

        return "dashboard"
    }


}
