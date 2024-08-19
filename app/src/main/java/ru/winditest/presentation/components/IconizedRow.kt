package ru.winditest.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconizedRow(
	modifier: Modifier = Modifier,
	painter: Painter,
	imagePosition: ImagePosition = ImagePosition.LEFT,
	imageWidth: Dp = 24.dp,
	imageHeight: Dp = 24.dp,
	contentDescription: String? = null,
	spacing: Dp = 8.dp,
	tint: Color = LocalContentColor.current,
	opacity: Float = 0.6f,
	content: @Composable () -> Unit
) {
	Row(
		modifier = modifier,
		verticalAlignment = Alignment.CenterVertically
	) {
		if (imagePosition == ImagePosition.RIGHT)
			content()
		Icon(
			modifier = Modifier
				.width(imageWidth)
				.height(imageHeight),
			painter = painter,
			contentDescription = contentDescription,
			tint = tint.copy(opacity)
		)
		Spacer(Modifier.width(spacing))
		if (imagePosition == ImagePosition.LEFT)
			content()
	}
}

@Composable
fun IconizedRow(
	imageVector: ImageVector,
	modifier: Modifier = Modifier,
	imagePosition: ImagePosition = ImagePosition.LEFT,
	imageWidth: Dp = 24.dp,
	imageHeight: Dp = 24.dp,
	spacing: Dp = 4.dp,
	opacity: Float = .6f,
	contentDescription: String? = null,
	verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
	tint: Color = LocalContentColor.current,
	content: @Composable () -> Unit
) {
	Row(
		modifier = modifier,
		verticalAlignment = verticalAlignment
	) {
		if (imagePosition == ImagePosition.RIGHT) {
			content()
			Spacer(modifier = Modifier.width(spacing))
		}
		Column(
			modifier = Modifier
				.padding(top = if (verticalAlignment == Alignment.Top) 4.dp else 0.dp),
			verticalArrangement = if (verticalAlignment == Alignment.Top) Arrangement.Top else Arrangement.Center
		) {
			Icon(
				modifier = Modifier
					.width(imageWidth)
					.height(imageHeight),
				imageVector = imageVector,
				contentDescription = contentDescription,
				tint = tint.copy(opacity)
			)
		}
		if (imagePosition == ImagePosition.LEFT) {
			Spacer(Modifier.width(spacing))
			content()
		}
	}
}

enum class ImagePosition {
	LEFT, RIGHT
}