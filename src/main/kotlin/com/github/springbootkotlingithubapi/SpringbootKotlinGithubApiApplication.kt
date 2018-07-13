package com.github.springbootkotlingithubapi

import com.github.springbootkotlingithubapi.configuration.GithubProperties
import com.github.springbootkotlingithubapi.domain.GithubProject
import com.github.springbootkotlingithubapi.repository.GithubProjectRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.support.beans
import java.util.stream.Stream
import kotlin.reflect.KClass

@SpringBootApplication
@EnableConfigurationProperties(GithubProperties::class)
class SpringbootKotlinGithubApiApplication

fun main(args: Array<String>) {
//    runApplication<SpringbootKotlinGithubApiApplication>(*args)

    val source: KClass<SpringbootKotlinGithubApiApplication> = SpringbootKotlinGithubApiApplication::class

    SpringApplicationBuilder()
            .sources(source.java) // //class referencess
            .initializers(beans())
            .run(*args)


}

fun beans() = beans {
    bean(name = "initialDataLoadRunner") {
        CommandLineRunner {
            val githubProjectRepository = ref<GithubProjectRepository>() // getting bean from context

            Stream.of("spring-projects,spring-data-jpa", "spring-projects,spring-boot")
                    .map { it.split(",") }
                    // Indexed access operator , named function
                    .map { githubProjectRepository.save(GithubProject(orgName = it[0], repoName = it[1])) }
                    .forEach(::println)

        }
    }
}

@FunctionalInterface
interface Converter {
    fun convert(s: String): Int
}