/*
 * Designed and developed by Duckie Team 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/quack-quack-android/blob/main/LICENSE
 */

@file:Suppress("unused")

package team.duckie.quackquack.util

import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEach

/**
 * 주어진 리스트에서 [T]로 캐스팅 가능한 첫 번째 원소를 반환합니다.
 * 모든 원소가 [T]로 캐스팅이 불가능하면 null을 반환합니다.
 */
public inline fun <reified T> List<*>.fastFirstIsInstanceOrNull(): T? =
  fastFirstOrNull { it is T } as? T

/**
 * 주어진 리스트에서 [T]로 캐스팅 가능한 모든 원소를 반환합니다.
 * [T]로 캐스팅 가능한 원소가 하나도 없다면 null을 반환합니다.
 */
public inline fun <reified T> List<*>.fastFilterIsInstanceOrNull(): List<T>? {
  val target = ArrayList<T>()
  fastForEach { element ->
    if (element is T) target += element
  }
  return target.ifEmpty { null }
}
