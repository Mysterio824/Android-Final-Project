import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.androidfinalproject.hacktok.ui.search.SearchAction
import com.androidfinalproject.hacktok.ui.search.SearchDashboardScreen
import com.androidfinalproject.hacktok.ui.search.SearchViewModel
import android.util.Log

@Composable
fun SearchDashboardScreenRoot(
    viewModel: SearchViewModel = hiltViewModel(),
    onUserClick: (String?) -> Unit,
    onPostClick: (String?) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val TAG = "SearchDashboardRoot"

    SearchDashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SearchAction.OnPostClick -> {
                    // First save the search term to history
                    viewModel.onAction(action)
                    // Then navigate to post
                    onPostClick(action.post.id)
                }
                is SearchAction.OnUserClick -> {
                    // First save the search term to history
                    viewModel.onAction(action)
                    Log.d(TAG, "Navigating to User Profile with userId: ${action.user.id}")
                    onUserClick(action.user.id)
                }
                is SearchAction.OnNavigateBack -> {
                    // First save the search term to history
                    viewModel.onAction(action)
                    // Then navigate back
                    onNavigateBack()
                }
                else -> viewModel.onAction(action)
            }
        }
    )
}