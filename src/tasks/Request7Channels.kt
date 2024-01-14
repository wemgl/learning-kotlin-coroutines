package tasks

import contributors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

suspend fun loadContributorsChannels(
    service: GitHubService,
    req: RequestData,
    updateResults: suspend (List<User>, completed: Boolean) -> Unit
) = coroutineScope {
    val repos = service
        .getOrgRepos(req.org) // Executes request and blocks the current thread
        .also { logRepos(req, it) }
        .body() ?: emptyList()

    val channel = Channel<List<User>>()
    repos.forEach { repo ->
        launch {
            val users = service
                .getRepoContributors(req.org, repo.name) // Executes request and blocks the current thread
                .also { logUsers(repo, it) }
                .bodyList()
            channel.send(users)
        }
    }

    var allUsers = listOf<User>()
    repeat(repos.size) {
        val users = channel.receive()
        allUsers = (allUsers + users).aggregate()
        updateResults(allUsers, it == repos.lastIndex)
    }
}
