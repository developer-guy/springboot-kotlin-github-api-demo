package com.github.springbootkotlingithubapi

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.beans
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.OffsetDateTime
import java.util.stream.Stream
import java.util.stream.StreamSupport
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import kotlin.collections.set
import kotlin.streams.toList

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

@ConfigurationProperties(prefix = "github")
class GithubProperties {
    /*
    *  A token to use Github Public API.
    * */
    var token: String? = null
    var rootUri: String? = null
}

@Controller
@RequestMapping("/issues")
class GithubIssuesController(private val githubClient: GithubClient,
                             private val githubProjectRepository: GithubProjectRepository) {

    @GetMapping("{orgName}/{repoName}/events")
    @ResponseBody
    fun findIssueEventsByOrgNameAndRepoName(@PathVariable orgName: String, @PathVariable repoName: String, pageable: Pageable): ResponseEntity<RepositoryEventsList> {
        val githubProject = githubProjectRepository.findByRepoName(repoName)

        return if (githubProject == null) {
            ResponseEntity
                    .notFound()
                    .build()
        } else {
            val responseEntity = githubClient.fetchEvents(githubProject.orgName, githubProject.repoName, pageable)

            val httpHeaders = HttpHeaders()

            val responseHeadersFromGithubAPI = responseEntity.headers

            httpHeaders["X-RateLimit-Limit"] = responseHeadersFromGithubAPI["X-RateLimit-Limit"]
            httpHeaders["X-RateLimit-Remaining"] = responseHeadersFromGithubAPI["X-RateLimit-Remaining"]
            httpHeaders["X-RateLimit-Reset"] = responseHeadersFromGithubAPI["X-RateLimit-Reset"]

            ResponseEntity
                    .ok()
                    .headers(responseHeadersFromGithubAPI)
                    .body(responseEntity.body)
        }
    }

    @GetMapping
    fun dashboardView(model: Model): String {
        val entries = StreamSupport.stream(this.githubProjectRepository.findAll().spliterator(), true)
                .map { DashboardEntry(it, githubClient.fetchEvents(orgName = it.orgName, repoName = it.repoName).body) }.toList()

        model["entries"] = entries

        return "dashboard"
    }


}


@RestController
@RequestMapping("/projects")
class GithubProjectRestController(private val githubProjectRepository: GithubProjectRepository) {

    @GetMapping
    fun all() = githubProjectRepository
            .findAll()
            .toList()
}

@Entity
data class GithubProject(
        @Id @GeneratedValue val id: Long? = null,
        val orgName: String? = null,
        @Column(unique = true) val repoName: String? = null

)

interface GithubProjectRepository : CrudRepository<GithubProject, Long> {
    fun findByRepoName(repoName: String): GithubProject?
}


@Configuration
class RestTemplateConfiguration {

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder,
                     properties: GithubProperties): RestTemplate = restTemplateBuilder
            .rootUri(properties.rootUri)
            .interceptors(TokenCheckInterceptor(properties.token))
            .build()


    class TokenCheckInterceptor(private val token: String? = null) : ClientHttpRequestInterceptor {

        override fun intercept(request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution): ClientHttpResponse {
            if (StringUtils.hasText(token)) {
                request.headers.add(HttpHeaders.AUTHORIZATION, "token $token")
            }

            return execution.execute(request, body)
        }

    }

}

class RepositoryEventsList : MutableList<RepositoryEvent> by ArrayList()

@Component
class GithubClient(val restTemplate: RestTemplate) {
    private val issuesURL = "/repos/{owner}/{repo}/issues/events?page={page}&per_page={per_page}"

    fun fetchEvents(orgName: String?, repoName: String?, pageable: Pageable = PageRequest.of(0, 20)): ResponseEntity<RepositoryEventsList> {
        val uriString = UriComponentsBuilder
                .fromUriString(issuesURL)
                .buildAndExpand(mapOf("owner" to orgName, "repo" to repoName, "page" to pageable.pageNumber, "per_page" to pageable.pageSize))
                .toUriString()


        return restTemplate.getForEntity(uriString, RepositoryEventsList::class.java)
    }

}


class EventToTypeDeSerializer(vc: Class<*>?) : StdDeserializer<Type?>(vc) {

    override fun deserialize(p0: JsonParser?, p1: DeserializationContext?): Type? {
        val node = p0!!.codec.readTree<JsonNode>(p0).asText()
        return Type.of(node)
    }

}

data class DashboardEntry(val project: GithubProject, val events: RepositoryEventsList?)


data class RepositoryEvent(@JsonProperty("event") @JsonDeserialize(using = EventToTypeDeSerializer::class) val type: Type,
                           @JsonProperty("created_at") val creationTime: OffsetDateTime,
                           @JsonProperty("actor") val actor: Actor,
                           @JsonProperty("issue") val issue: Issue)

enum class Type(val type: String) {
    CLOSED("closed"),
    REOPENED("reopened"),
    SUBSCRIBED("subscribed"),
    UNSUBSCRIBED("unsubscribed"),
    MERGED("merged"),
    REFERENCED("referenced"),
    MENTIONED("mentioned"),
    ASSIGNED("assigned"),
    UNASSIGNED("unassigned"),
    LABELED("labeled"),
    UNLABELED("unlabeled"),
    MILESTONED("milestoned"),
    DEMILESTONED("demilestoned"),
    RENAMED("renamed"),
    LOCKED("locked"),
    UNLOCKED("unlocked"),
    HEAD_REF_DELETED("head_ref_deleted"),
    HEAD_REF_RESTORED("head_ref_restored"),
    CONVERTED_NOTE_TO_ISSUE("converted_note_to_issue"),
    MOVED_COLUMNS_IN_PROJECT("moved_columns_in_project"),
    COMMENT_DELETED("comment_deleted"),
    REVIEW_REQUESTED("review_requested");

    companion object {
        fun of(type: String): Type? = enumValues<Type>()
                .filter { it.type == type }
                .singleOrNull()
    }
}

data class Issue(@JsonProperty("html_url") val htmlUrl: String,
                 @JsonProperty("number") val number: Int,
                 @JsonProperty("title") val title: String)


data class Actor(@JsonProperty("login") val login: String,
                 @JsonProperty("avatar_url") val avatarUrl: String,
                 @JsonProperty("html_url") val htmlUrl: String)

