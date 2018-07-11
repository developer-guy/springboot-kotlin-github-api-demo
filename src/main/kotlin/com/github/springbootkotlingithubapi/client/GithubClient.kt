package com.github.springbootkotlingithubapi.client

import com.github.springbootkotlingithubapi.model.RepositoryEvents
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder


@Component
class GithubClient(val restTemplate: RestTemplate) {
    private val issuesURL = "/repos/{owner}/{repo}/issues/events?page={page}&per_page={per_page}"

    fun fetchEvents(orgName: String?,
                    repoName: String?,
                    pageable: Pageable = PageRequest.of(0, 20)): ResponseEntity<RepositoryEvents> {
        val uriVariables = mapOf("owner" to orgName,
                "repo" to repoName,
                "page" to pageable.pageNumber,
                "per_page" to pageable.pageSize)

        val uriString = UriComponentsBuilder
                .fromUriString(issuesURL)
                .buildAndExpand(uriVariables)
                .toUriString()


        return restTemplate.getForEntity(uriString, RepositoryEvents::class.java)
    }

}