#!/bin/bash
cat << 'EOF' >> gradle/libs.versions.toml
kotlinxSerialization = "1.8.0"

[libraries.kotlinx-serialization-json]
group = "org.jetbrains.kotlinx"
name = "kotlinx-serialization-json"
version.ref = "kotlinxSerialization"

[libraries.retrofit-converter-kotlinx-serialization]
group = "com.squareup.retrofit2"
name = "converter-kotlinx-serialization"
version.ref = "retrofit"

[plugins.kotlin-serialization]
id = "org.jetbrains.kotlin.plugin.serialization"
version.ref = "kotlin"
EOF

sed -i 's/alias(libs.plugins.kotlin.compose)/alias(libs.plugins.kotlin.compose)\n  alias(libs.plugins.kotlin.serialization)/' app/build.gradle.kts

sed -i 's/\/\/ implementation(libs.androidx.navigation.compose)/implementation(libs.androidx.navigation.compose)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.coil.compose)/implementation(libs.coil.compose)/' app/build.gradle.kts
sed -i 's/\/\/ implementation(libs.androidx.compose.material.icons.extended)/implementation(libs.androidx.compose.material.icons.extended)/' app/build.gradle.kts
sed -i 's/implementation(libs.retrofit)/implementation(libs.retrofit)\n  implementation(libs.kotlinx.serialization.json)\n  implementation(libs.retrofit.converter.kotlinx.serialization)/' app/build.gradle.kts
