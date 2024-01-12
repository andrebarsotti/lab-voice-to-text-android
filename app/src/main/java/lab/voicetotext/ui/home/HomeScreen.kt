package lab.voicetotext.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import lab.voicetotext.data.states.VoiceToTextParserState
import lab.voicetotext.ui.AppViewModelProvider
import lab.voicetotext.utils.setupRecordPermisssions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val canRecord = setupRecordPermisssions(context)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val state = viewModel.voiceState.collectAsState().value

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (canRecord) {
                        if (!state.isSpeaking) {
                            viewModel.stopListening()
                        }
                        else {
                            viewModel.stopListening()
                        }
                    }
                }
            ) {
                AnimatedContent(targetState = state.isSpeaking, label = "") { isSpeaking->
                    if (isSpeaking) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null
                        )
                    }
                    else {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null
                        )
                    }
                }
            }
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    )
    { innerPadding ->
        HomeBody(
            state = state,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp)
        )
    }
}

@Composable
fun HomeBody(
    state: VoiceToTextParserState,
    modifier: Modifier = Modifier
) {
    Column (
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(targetState = state.isSpeaking, label = "") { isSpeaking ->
            if (isSpeaking) {
                Text(
                    text = "Fale...",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            else {
                Text(
                    text = state.spokenText.ifEmpty { "Clique no 'play' para falar" },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
