package com.github.springbootkotlingithubapi

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/projects")
class GithubProjectRestController(private val githubProjectRepository: GithubProjectRepository) {

    @GetMapping
    fun all() = githubProjectRepository
            .findAll()
            .toList()
}
