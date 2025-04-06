import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.mainDashboard.search.SearchAction
import com.androidfinalproject.hacktok.ui.mainDashboard.search.SearchDashboardScreen
import com.androidfinalproject.hacktok.ui.mainDashboard.search.SearchViewModel

@Composable
fun SearchDashboardScreenRoot(
    viewModel: SearchViewModel,
    onUserClick: (String?) -> Unit,
    onPostClick: (String?) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    SearchDashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SearchAction.OnPostClick -> onPostClick(action.post.id)
                is SearchAction.OnUserClick -> onUserClick(action.user.id)
                else -> viewModel.onAction(action)
            }
        }
    )
}