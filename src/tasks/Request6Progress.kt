package tasks

import contributors.*

suspend fun loadContributorsProgress(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) {
    val repos = service
        .getOrgRepos(req.org) // Executes request and blocks the current thread
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    var allResults = listOf<User>()
    repos.forEachIndexed { index, repo ->
        val results = service
            .getRepoContributors(req.org, repo.name) // Executes request and blocks the current thread
            .also { logUsers(repo, it) }
            .bodyList()
        allResults = (allResults + results).aggregate()
        updateResults(allResults, index == repos.lastIndex)
    }
}
