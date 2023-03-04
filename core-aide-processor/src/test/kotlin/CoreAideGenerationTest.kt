/*
 * Designed and developed by Duckie Team, 2022~2023
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/duckie-quack-quack/blob/main/LICENSE
 */

@file:Suppress("RedundantUnitReturnType", "RedundantVisibilityModifier", "RedundantUnitExpression")

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.withNotNull

class CoreAideGenerationTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun modifier() {
        val compilation = prepareCompilation(
            kotlin(
                "text.kt",
                """
                import team.duckie.quackquack.aide.annotation.TypedModifier
                import androidx.compose.ui.Modifier
    
                @TypedModifier
                public fun Modifier.span(): Modifier = Modifier
    
                @TypedModifier
                public fun Modifier.spans() = this
    
                @TypedModifier
                public fun Modifier.spans(duplicate: Any): Modifier {
                    return Modifier
                }
    
                @TypedModifier
                public fun emptyModifier() {}
    
                @TypedModifier
                public fun Modifier.noReturnModifier(): Unit {
                    return Unit
                }
    
                @TypedModifier
                private fun Modifier.privateModifier(): Modifier {
                    return Modifier
                }
                """,
            ),
            kotlin(
                "button.kt",
                """
                import team.duckie.quackquack.aide.annotation.TypedModifier
                import androidx.compose.ui.Modifier
    
                @TypedModifier
                public fun Modifier.click(): Modifier {
                    return Modifier
                }

                @TypedModifier
                public fun Modifier.longClick(): Modifier = Modifier
    
                @TypedModifier
                public fun Modifier.doubleClick(): Modifier = this
    
                // return type 생략시 type mismatch: Modifier & Modifier.Companion
                @TypedModifier
                public fun Modifier.typeMismatchModifier() = Modifier
    
                @TypedModifier
                public fun Modifier.noReturnModifier2(): Unit {}

                @TypedModifier
                internal fun Modifier.internalModifier(): Modifier {
                    return Modifier
                }
                """,
            ),
        )
        val result = compilation.compile()

        expectThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val aideModifiersKt = compilation
            .kspSourcesDir
            .walkTopDown()
            .firstOrNull { file ->
                file.name == "AideModifiers.kt"
            }

        expectThat(aideModifiersKt).withNotNull {
            get(File::readText).isKtEqualTo(
                """
                import kotlin.String
                import kotlin.collections.List
                import kotlin.collections.Map

                internal val aideModifiers: Map<String, List<String>> = run {
                  val aide = mutableMapOf<String, List<String>>()
                  aide["button"] = listOf("click", "longClick", "doubleClick")
                  aide["text"] = listOf("span", "spans")
                  aide
                }

                
                """.trimIndent(),
            )
        }
    }

    @Test
    fun component() {
        val compilation = prepareCompilation(
            kotlin(
                "text.kt",
                """
                import androidx.compose.runtime.Composable
                import androidx.compose.ui.Modifier
    
                @Composable
                public fun QuackText() {}
    
                @Composable
                public fun Text() {}
    
                @Composable
                public fun Modifier.QuackText2() {}
    
                @Composable
                public fun QuackText3(): Int = 1
    
                @Composable
                public fun QuackText4() = 1

                @Composable
                private fun QuackText5() {}
                """,
            ),
            kotlin(
                "button.kt",
                """
                import androidx.compose.runtime.Composable
                import androidx.compose.ui.Modifier
    
                @Composable
                public fun QuackButton() {}
    
                @Composable
                public fun Button() {}
    
                @Composable
                public fun QuackButton2() {}

                @Composable
                public fun QuackButton2(duplicate: Any) {}
    
                @Composable
                public fun QuackButton3(): Int = 1

                @Composable
                public fun Modifier.QuackButton4() {}
    
                @Composable
                internal fun QuackButton5() {}
                """,
            ),
        )
        val result = compilation.compile()

        expectThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

        val aideComponentsKt = compilation
            .kspSourcesDir
            .walkTopDown()
            .firstOrNull { file ->
                file.name == "AideComponents.kt"
            }

        expectThat(aideComponentsKt).withNotNull {
            get(File::readText).isKtEqualTo(
                """
                import kotlin.String
                import kotlin.collections.List
                import kotlin.collections.Map
                
                internal val aideComponents: Map<String, List<String>> = run {
                  val aide = mutableMapOf<String, List<String>>()
                  aide["button"] = listOf("QuackButton", "QuackButton2")
                  aide["text"] = listOf("QuackText")
                  aide
                }


                """.trimIndent(),
            )
        }
    }

    private fun prepareCompilation(vararg sourceFiles: SourceFile): KotlinCompilation {
        return KotlinCompilation().apply {
            workingDir = temporaryFolder.root
            sources = sourceFiles.asList().plus(stubs)
            allWarningsAsErrors = true
            symbolProcessorProviders = listOf(QuackCoreAideSymbolProcessorProvider())
        }
    }
}
