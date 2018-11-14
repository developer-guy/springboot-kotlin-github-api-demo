package com.github.springbootkotlingithubapi.jpa

import com.github.springbootkotlingithubapi.domain.GithubProject
import com.github.springbootkotlingithubapi.repository.GithubProjectRepository
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@DataJpaTest
class GithubProjectRepositoryTest {

    @Autowired
    lateinit var testEntityManager: TestEntityManager

    @Autowired
    lateinit var githubProjectRepository: GithubProjectRepository

    @Test
    fun `It Should Find Github Project By Repository Name`() {
        // Arrange
        val githubProject = GithubProject("spring-data", "spring-boot")

        testEntityManager.persistAndFlush(githubProject)

        // Act
        val retrievedGithubProject = githubProjectRepository.findByRepoName("spring-data")

        // Assert
        retrievedGithubProject shouldNotBe null
        retrievedGithubProject?.orgName shouldBe "spring-boot"
    }
}