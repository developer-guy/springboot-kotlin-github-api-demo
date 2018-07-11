package com.github.springbootkotlingithubapi.repository

import com.github.springbootkotlingithubapi.domain.GithubProject
import org.springframework.data.repository.CrudRepository

interface GithubProjectRepository : CrudRepository<GithubProject, Long> {
    fun findByRepoName(repoName: String?): GithubProject?
}