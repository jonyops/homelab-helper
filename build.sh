#!/usr/bin/env bash
# Linux twin of build.ps1, used by the release workflow. Needs ANDROID_HOME with
# build-tools 36.0.0 and platforms/android-36, a JDK 17, and the signing key in
# KEYSTORE_FILE + KEYSTORE_PASS (alias "helper").
set -euo pipefail
here="$(cd "$(dirname "$0")" && pwd)"
bt="$ANDROID_HOME/build-tools/36.0.0"
jar="$ANDROID_HOME/platforms/android-36/android.jar"
out="$here/build"
read -r code name < "$here/version.txt"

rm -rf "$out" && mkdir -p "$out/classes" "$out/dex"
"$bt/aapt2" link -o "$out/base.apk" --manifest "$here/AndroidManifest.xml" -I "$jar" \
  -A "$here/assets" --min-sdk-version 26 --target-sdk-version 36 \
  --version-code "$code" --version-name "$name"
find "$here/src" -name '*.java' > "$out/sources.txt"
javac -nowarn -source 11 -target 11 -classpath "$jar" -d "$out/classes" @"$out/sources.txt"
"$bt/d8" --lib "$jar" --min-api 26 --output "$out/dex" $(find "$out/classes" -name '*.class')
(cd "$out/dex" && zip -q "$out/base.apk" classes.dex)
"$bt/zipalign" -f 4 "$out/base.apk" "$out/aligned.apk"
"$bt/apksigner" sign --ks "$KEYSTORE_FILE" --ks-key-alias helper \
  --ks-pass env:KEYSTORE_PASS --key-pass env:KEYSTORE_PASS \
  --out "$here/homelab-helper.apk" "$out/aligned.apk"
"$bt/apksigner" verify --print-certs "$here/homelab-helper.apk" | head -2
ls -l "$here/homelab-helper.apk"
