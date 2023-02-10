/*
 * Designed and developed by Duckie Team, 2022
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/duckie-team/quack-quack-android/blob/master/LICENSE
 */

// ISSUE: https://github.com/sungbinland/duckie-quack-quack/issues/114

plugins {
    id(ConventionEnum.AndroidLibrary)
    // id(ConventionEnum.AndroidQuackPublish)
}

android {
    namespace = "team.duckie.quackquack.publish.bom"
}

/*quackArtifactPublish {
    type = team.duckie.quackquack.convention.QuackArtifactType.Bom
}*/
