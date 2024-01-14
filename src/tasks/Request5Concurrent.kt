package tasks

import contributors.*
import kotlinx.coroutines.*

suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    val deferredRepos = async {
        service
            .getOrgRepos(req.org) // Executes request and blocks the current thread
            .also { logRepos(req, it) }
            .body() ?: emptyList()
    }

    val repos = deferredRepos.await()

    repos.map { repo ->
        async {
            log("Starting loading for ${repo.name}")
            delay(3000L)
            service
                .getRepoContributors(req.org, repo.name) // Executes request and blocks the current thread
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }.awaitAll()
        .flatten()
        .aggregate()
}
