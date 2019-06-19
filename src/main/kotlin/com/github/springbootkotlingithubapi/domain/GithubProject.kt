package com.github.springbootkotlingithubapi.domain

import javax.persistence.*

@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["repoName"])])
@Entity
data class GithubProject constructor( //constructor is optional!!
        val repoName: String? = null,
        val orgName: String? = null,
        @Id @GeneratedValue val id: Long? = null
)