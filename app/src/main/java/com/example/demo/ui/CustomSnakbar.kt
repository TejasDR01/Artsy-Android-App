package com.example.demo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CustomSnackbar() {
    val snackbarManager = SnackbarManager.getInstance()
    val message = snackbarManager.message.collectAsState()
    val isVisible = snackbarManager.isVisible.collectAsState()

    message.value?.let {
        AnimatedSnackbar(
            message = it,
            isShowing = isVisible.value,
            onDismiss = { snackbarManager.dismiss() }
        )
    }
}

@Composable
fun AnimatedSnackbar(
    message: String,
    isShowing: Boolean,
    onDismiss: () -> Unit
) {
    val state = remember {
        MutableTransitionState(false).apply {
            targetState = isShowing
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visibleState = state,
            enter = fadeIn(animationSpec = tween(300)) +
                    expandVertically(animationSpec = tween(300), expandFrom = Alignment.Bottom),
            exit = fadeOut(animationSpec = tween(300)) +
                    shrinkVertically(animationSpec = tween(300), shrinkTowards = Alignment.Bottom)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}