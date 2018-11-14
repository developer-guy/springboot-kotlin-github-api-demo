package com.github.springbootkotlingithubapi.startup

import com.github.springbootkotlingithubapi.domain.GithubProject
import com.github.springbootkotlingithubapi.repository.GithubProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.stream.Stream

@Component
class DataLoaderRunner : CommandLineRunner {

    private val organizationRepos: Array<String> = arrayOf("spring-projects,spring-data-jpa", "spring-projects,spring-boot")

    @Autowired
    lateinit var githubProjectRepository: GithubProjectRepository

    override fun run(vararg args: String?) {
        Stream.of(*organizationRepos)
                .map { it.split(",") }
                .map {githubProjectRepository.save(GithubProject(orgName = it[0], repoName = it[1])) }
                .forEach(::println)
    }
}