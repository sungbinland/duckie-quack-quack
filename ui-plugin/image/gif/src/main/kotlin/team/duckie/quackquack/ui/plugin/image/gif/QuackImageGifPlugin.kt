/*
 * Designed and developed by Duckie Team 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/quack-quack-android/blob/main/LICENSE
 */

package team.duckie.quackquack.ui.plugin.image.gif

import android.os.Build
import androidx.compose.runtime.Stable
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import team.duckie.quackquack.ui.plugin.image.QuackImagePlugin

@Stable
public val QuackImageGifPlugin: QuackImagePlugin.CoilImageLoader = QuackImagePlugin.CoilImageLoader { _, _, _, _ ->
  components {
    add(
      if (Build.VERSION.SDK_INT >= 28) ImageDecoderDecoder.Factory()
      else GifDecoder.Factory()
    )
  }
}
