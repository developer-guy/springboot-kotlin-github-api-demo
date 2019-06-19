package com.github.springbootkotlingithubapi.startup

import com.github.springbootkotlingithubapi.domain.GithubProject
import com.github.springbootkotlingithubapi.repository.GithubProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.util.*
import java.util.stream.Stream

@Component
class DataLoaderRunner : CommandLineRunner {

    private val organizationRepos: Array<String> = arrayOf("spring-projects,spring-data-jpa", "spring-projects,spring-boot")

    @Autowired
    lateinit var githubProjectRepository: GithubProjectRepository

    override fun run(vararg args: String?) {
        Stream.of(*organizationRepos)
                .map { it.split(",") }
                .forEach {
                    val orgName = it[0]
                    val repoName = it[1]
                    val project: GithubProject? = githubProjectRepository.findByRepoName(repoName = repoName)
                    project whenNull {
                        githubProjectRepository.save(GithubProject(orgName = orgName, repoName = repoName))
                    }
                }
    }
}

infix fun Any?.whenNull(callback: () -> Unit) {
    if (Objects.isNull(this)) {
        callback.invoke()
    }
}