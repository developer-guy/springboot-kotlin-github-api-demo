package com.github.springbootkotlingithubapi

import com.github.springbootkotlingithubapi.configuration.GithubProperties
import com.github.springbootkotlingithubapi.domain.GithubProject
import com.github.springbootkotlingithubapi.repository.GithubProjectRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.support.beans
import java.util.stream.Stream

@SpringBootApplication
@EnableConfigurationProperties(GithubProperties::class)
class SpringbootKotlinGithubApiApplication

fun main(args: Array<String>) {
//    runApplication<SpringbootKotlinGithubApiApplication>(*args)

    SpringApplicationBuilder()
            .sources(SpringbootKotlinGithubApiApplication::class.java)
            .initializers(beans {
                bean {
                    // bean definition

                    val githubProjectRepository = ref<GithubProjectRepository>() // getting bean from context

                    Stream.of("spring-projects,spring-data-jpa",
                            "spring-projects,spring-boot")
                            .map { it.split(",") }
                            .forEach {
                                githubProjectRepository.save(GithubProject(orgName = it[0], repoName = it[1]))
                            }
                }
            })
            .run(*args)

}