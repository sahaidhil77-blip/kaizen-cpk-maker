#!/usr/bin/env bash
set -euo pipefail
A=android/app/src/main
J=$A/java/com/kaizen/app
mkdir -p "$J"
cp patch/MainActivity.java "$J/MainActivity.java"
cp patch/OpenerPlugin.java "$J/OpenerPlugin.java"

M=$A/AndroidManifest.xml
# izin penyimpanan (dipakai Android 10 ke bawah; Android 11+ tidak perlu)
if ! grep -q READ_EXTERNAL_STORAGE "$M"; then
  sed -i 's#<application#<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />\n    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="29" />\n    <application#' "$M"
fi
if ! grep -q requestLegacyExternalStorage "$M"; then
  sed -i '0,/<application/s#<application#<application android:requestLegacyExternalStorage="true"#' "$M"
fi
echo "--- manifest ---"; cat "$M"
