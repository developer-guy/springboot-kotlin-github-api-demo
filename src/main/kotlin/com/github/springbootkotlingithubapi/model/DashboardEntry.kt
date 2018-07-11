package com.github.springbootkotlingithubapi.model

import com.github.springbootkotlingithubapi.domain.GithubProject

data class DashboardEntry(val project: GithubProject,
                          val events: RepositoryEvents?)
