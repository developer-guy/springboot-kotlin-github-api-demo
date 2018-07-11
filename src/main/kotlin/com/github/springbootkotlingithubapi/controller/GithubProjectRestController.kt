package com.github.springbootkotlingithubapi.controller

import com.github.springbootkotlingithubapi.repository.GithubProjectRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/projects")
class GithubProjectRestController constructor(private val githubProjectRepository: GithubProjectRepository) {

    @GetMapping
    fun all() = githubProjectRepository
            .findAll()
            .toList()
}
