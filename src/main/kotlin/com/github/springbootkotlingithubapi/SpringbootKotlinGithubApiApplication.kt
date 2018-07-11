package com.github.springbootkotlingithubapi

import org.springframework.boot.CommandLineRunner
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
                    CommandLineRunner {
                        val githubProjectRepository = ref<GithubProjectRepository>()

                        Stream.of("spring-projects,spring-boot",
                                "spring-projects,spring-data-jpa")
                                .map { it.split(",") }
                                .forEach { githubProjectRepository.save(GithubProject(orgName = it[0], repoName = it[1])) }
                    }
                }
            })
            .run(*args)

}