import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidfinalproject.hacktok.ui.search.SearchAction
import com.androidfinalproject.hacktok.ui.search.SearchDashboardScreen
import com.androidfinalproject.hacktok.ui.search.SearchViewModel

@Composable
fun SearchDashboardScreenRoot(
    viewModel: SearchViewModel = hiltViewModel(),
    onUserClick: (String?) -> Unit,
    onPostClick: (String?) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    SearchDashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SearchAction.OnPostClick -> onPostClick(action.post.id)
                is SearchAction.OnUserClick -> onUserClick(action.user.id)
                is SearchAction.OnNavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}