package com.github.springbootkotlingithubapi.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class GithubProject constructor( //constructor is optional!!
        @Column(unique = true) val repoName: String? = null,
        val orgName: String? = null,
        @Id @GeneratedValue val id: Long? = null
)