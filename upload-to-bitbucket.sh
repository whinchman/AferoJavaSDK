#!/bin/bash

# Upload Afero SDK Android release to Bitbucket Downloads
# Usage: ./upload-to-bitbucket.sh [version]

VERSION=${1:-"2.0.0-espresso"}
BITBUCKET_USER=${BITBUCKET_USER:-"your-username"}
BITBUCKET_REPO=${BITBUCKET_REPO:-"AferoJavaSDK"} 
BITBUCKET_APP_PASSWORD=${BITBUCKET_APP_PASSWORD:-""}

if [ -z "$BITBUCKET_APP_PASSWORD" ]; then
    echo "❌ Error: BITBUCKET_APP_PASSWORD environment variable not set"
    echo "Create an app password at: https://bitbucket.org/account/settings/app-passwords/"
    echo "Then export BITBUCKET_APP_PASSWORD=your-app-password"
    exit 1
fi

ARCHIVE_FILE="build/afero-sdk-android-${VERSION}.zip"

if [ ! -f "$ARCHIVE_FILE" ]; then
    echo "❌ Error: Archive file not found: $ARCHIVE_FILE"
    echo "Run: ./gradlew assembleAndroidRelease -PreleaseVersion=${VERSION}"
    exit 1
fi

echo "📤 Uploading Afero SDK Android ${VERSION} to Bitbucket..."
echo "📁 File: $ARCHIVE_FILE"
echo "🔗 Repository: ${BITBUCKET_USER}/${BITBUCKET_REPO}"

# Upload to Bitbucket Downloads
UPLOAD_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
    -u "${BITBUCKET_USER}:${BITBUCKET_APP_PASSWORD}" \
    -F "files=@${ARCHIVE_FILE}" \
    "https://api.bitbucket.org/2.0/repositories/${BITBUCKET_USER}/${BITBUCKET_REPO}/downloads")

HTTP_CODE=$(echo "$UPLOAD_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$UPLOAD_RESPONSE" | head -n -1)

if [ "$HTTP_CODE" = "201" ]; then
    echo "✅ Upload successful!"
    echo "🌐 Download URL: https://bitbucket.org/${BITBUCKET_USER}/${BITBUCKET_REPO}/downloads/"
    echo ""
    echo "📱 To use in your Android project:"
    echo "   1. Download afero-sdk-android-${VERSION}.zip"
    echo "   2. Extract to your Android project"
    echo "   3. Follow the README-Android.md instructions"
    echo ""
    echo "☕ Your espresso maker Android app is ready to update!"
else
    echo "❌ Upload failed with HTTP code: $HTTP_CODE"
    echo "Response: $RESPONSE_BODY"
    echo ""
    echo "💡 Troubleshooting:"
    echo "   • Check your app password has 'Downloads' permissions"
    echo "   • Verify repository name: ${BITBUCKET_USER}/${BITBUCKET_REPO}"
    echo "   • Ensure you have write access to the repository"
fi
