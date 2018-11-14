package com.github.springbootkotlingithubapi.model

import io.kotlintest.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@JsonTest
class IssueJsonTest {

    @Autowired
    private lateinit var jacksonTester: JacksonTester<Issue>

    @Test
    fun `It Should De-Serialize Successfully`() {
        val jsonContent = "{  \n" +
                "   \"html_url\":\"https://github.com/spring-projects/spring-data-jpa/pull/302\",\n" +
                "   \"number\":302,\n" +
                "   \"title\":\"DATAJPA-1451 - Inconsistent Assert messages. #302\"\n" +
                "}"

        val issue = jacksonTester.parse(jsonContent).`object`

        issue.htmlUrl shouldBe "https://github.com/spring-projects/spring-data-jpa/pull/302"
        issue.number shouldBe 302
        issue.title shouldBe "DATAJPA-1451 - Inconsistent Assert messages. #302"
    }
}