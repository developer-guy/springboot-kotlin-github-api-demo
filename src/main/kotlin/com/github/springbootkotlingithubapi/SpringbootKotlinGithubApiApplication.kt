package com.github.springbootkotlingithubapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringbootKotlinGithubApiApplication

fun main(args: Array<String>) {
    runApplication<SpringbootKotlinGithubApiApplication>(*args)

    /* val source: KClass<SpringbootKotlinGithubApiApplication> = SpringbootKotlinGithubApiApplication::class

     SpringApplicationBuilder()
             .sources(source.java) // //class referencess
             .initializers(beansInitializer())
             .run(*args)*/


}

/*
fun beansInitializer() = beans {
    this.bean(name = "initialDataLoadRunner") {
        val githubProjectRepository = ref<GithubProjectRepository>() // getting bean from context

        Stream.of(*organizationRepos)
                .map { it.split(",") }
                // Indexed access operator , named function
                .map {
                    if (Objects.isNull(githubProjectRepository.findByRepoName(repoName = it[1]))) {
                        githubProjectRepository.save(GithubProject(orgName = it[0], repoName = it[1]))
                    }
                }
                .forEach(::println)

    }
}*/
