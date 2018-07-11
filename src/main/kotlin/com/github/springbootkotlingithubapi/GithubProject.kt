package com.github.springbootkotlingithubapi

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class GithubProject(
        @Id @GeneratedValue val id: Long? = null,
        val orgName: String? = null,
        @Column(unique = true) val repoName: String? = null

)