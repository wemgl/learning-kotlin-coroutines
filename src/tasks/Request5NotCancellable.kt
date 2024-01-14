package tasks

import contributors.*
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

suspend fun loadContributorsNotCancellable(service: GitHubService, req: RequestData): List<User> {
    val deferredRepos = GlobalScope.async {
        service
            .getOrgRepos(req.org) // Executes request and blocks the current thread
            .also { logRepos(req, it) }
            .body() ?: emptyList()
    }

    val repos = deferredRepos.await()

    return repos.map { repo ->
        GlobalScope.async {
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
