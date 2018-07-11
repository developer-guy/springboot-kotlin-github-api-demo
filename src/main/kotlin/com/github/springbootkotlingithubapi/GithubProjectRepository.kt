package com.github.springbootkotlingithubapi

import org.springframework.data.repository.CrudRepository


interface GithubProjectRepository : CrudRepository<GithubProject, Long> {
    fun findByRepoName(repoName: String): GithubProject?
}