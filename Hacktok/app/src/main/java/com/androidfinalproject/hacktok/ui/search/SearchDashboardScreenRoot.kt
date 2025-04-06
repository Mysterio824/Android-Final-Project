import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.search.SearchAction
import com.androidfinalproject.hacktok.ui.search.SearchDashboardScreen
import com.androidfinalproject.hacktok.ui.search.SearchViewModel

@Composable
fun SearchDashboardScreenRoot(
    viewModel: SearchViewModel,
    onUserClick: (User) -> Unit,
    onPostClick: (Post) -> Unit,
    onGoBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    SearchDashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SearchAction.OnPostClick -> onPostClick(action.post)
                is SearchAction.OnUserClick -> onUserClick(action.user)
                else -> viewModel.onAction(action)
            }
        }
    )
}