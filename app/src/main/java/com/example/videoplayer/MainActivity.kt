package com.example.videoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.media3.ui.compose.ContentFrame

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VideoScreen()
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
internal fun VideoScreen() {
    val context = LocalContext.current
    val videoUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"

    // Create and remember the player; release it when leaving composition.
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = true
        }
    }
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    // Toggle between the Compose-native ContentFrame and the classic PlayerView.
    // Shows the bug when `true` on Android 11.
    val useContentFrame = true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val playerModifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)

        if (useContentFrame) {
            ContentFrame(
                player = player,
                modifier = playerModifier,
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                    }
                },
                modifier = playerModifier,
            )
        }

        Text(
            text = stringResource(R.string.video_title),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.video_subtitle),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.video_description),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
