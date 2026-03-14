package com.fotolicht.app

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FotolichtTheme {
                LightScreen()
            }
        }
    }
}

@Composable
private fun LightScreen() {
    var hue by remember { mutableFloatStateOf(48f) }
    var brightness by remember { mutableFloatStateOf(1f) }
    var showControls by remember { mutableStateOf(false) }
    val brightnessController = rememberBrightnessController()

    LaunchedEffect(brightness) {
        brightnessController.setScreenBrightness(brightness)
    }

    DisposableEffect(brightnessController) {
        onDispose {
            brightnessController.restore()
        }
    }

    val activeColor = Color.hsv(
        hue = hue,
        saturation = 1f,
        value = brightness.coerceIn(0f, 1f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(activeColor)
            .pointerInput(Unit) {
                detectTapGestures {
                    showControls = !showControls
                }
            }
    ) {
        if (showControls) {
            ControlPanel(
                hue = hue,
                brightness = brightness,
                onHueChange = { hue = it },
                onBrightnessChange = { brightness = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(20.dp)
            )
        }
    }
}

@Composable
private fun ControlPanel(
    hue: Float,
    brightness: Float,
    onHueChange: (Float) -> Unit,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {})
            },
        shape = RoundedCornerShape(28.dp),
        color = Color(0xB31B1B1B),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Light Controls",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = Color.hsv(hue, 1f, brightness.coerceIn(0.25f, 1f)),
                            shape = CircleShape
                        )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Brightness",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                Slider(
                    value = brightness,
                    onValueChange = onBrightnessChange,
                    valueRange = 0f..1f
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                HueSlider(
                    hue = hue,
                    onHueChange = onHueChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Text(
                text = "Tap outside this panel to hide the controls.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFD2D2D2)
            )
        }
    }
}

@Composable
private fun HueSlider(
    hue: Float,
    onHueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val spectrum = remember {
        listOf(
            Color.hsv(0f, 1f, 1f),
            Color.hsv(60f, 1f, 1f),
            Color.hsv(120f, 1f, 1f),
            Color.hsv(180f, 1f, 1f),
            Color.hsv(240f, 1f, 1f),
            Color.hsv(300f, 1f, 1f),
            Color.hsv(360f, 1f, 1f)
        )
    }

    Canvas(
        modifier = modifier
            .height(44.dp)
            .semantics { contentDescription = "Color spectrum slider" }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val width = size.width.coerceAtLeast(1)
                    onHueChange(((offset.x / width) * 360f).coerceIn(0f, 360f))
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val width = size.width.coerceAtLeast(1)
                    onHueChange(((change.position.x / width) * 360f).coerceIn(0f, 360f))
                }
            }
    ) {
        val strokeWidth = size.height * 0.45f
        val centerY = size.height / 2f
        val handleX = (hue / 360f) * size.width

        drawLine(
            brush = Brush.horizontalGradient(spectrum),
            start = Offset(0f, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = Color.White,
            radius = size.height * 0.24f,
            center = Offset(handleX, centerY)
        )
        drawCircle(
            color = Color.hsv(hue, 1f, 1f),
            radius = size.height * 0.16f,
            center = Offset(handleX, centerY)
        )
    }
}

@Composable
private fun FotolichtTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}

@Stable
private class BrightnessController(
    private val setBrightness: (Float) -> Unit,
    private val restoreBrightness: () -> Unit
) {
    fun setScreenBrightness(value: Float) {
        setBrightness(value.coerceIn(0f, 1f))
    }

    fun restore() {
        restoreBrightness()
    }
}

@Composable
private fun rememberBrightnessController(): BrightnessController {
    if (LocalInspectionMode.current) {
        return remember {
            BrightnessController(
                setBrightness = {},
                restoreBrightness = {}
            )
        }
    }

    val activity = LocalContext.current.findActivity()
    return remember(activity) {
        val originalBrightness = activity.window.attributes.screenBrightness
        BrightnessController(
            setBrightness = { value ->
                val params = activity.window.attributes
                params.screenBrightness = value
                activity.window.attributes = params
            },
            restoreBrightness = {
                val params = activity.window.attributes
                params.screenBrightness = originalBrightness
                activity.window.attributes = params
            }
        )
    }
}

private tailrec fun Context.findActivity(): Activity =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> error("Unable to find Activity from context")
    }

@Preview(showBackground = true)
@Composable
private fun LightScreenPreview() {
    FotolichtTheme {
        LightScreen()
    }
}
