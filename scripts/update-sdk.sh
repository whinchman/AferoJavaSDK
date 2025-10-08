#!/bin/bash
# update-sdk.sh - Script for IoT devices to pull SDK updates from Bitbucket

SDK_VERSION=${1:-"latest"}
BITBUCKET_USER=${BITBUCKET_USER:-"your-username"}
BITBUCKET_REPO=${BITBUCKET_REPO:-"AferoJavaSDK"}
BITBUCKET_APP_PASSWORD=${BITBUCKET_APP_PASSWORD:-""}
LOCAL_SDK_PATH="/opt/afero-sdk"

echo "Updating Afero SDK to version: $SDK_VERSION"

# Create backup of current version
if [ -d "$LOCAL_SDK_PATH" ]; then
    cp -r "$LOCAL_SDK_PATH" "${LOCAL_SDK_PATH}.backup.$(date +%Y%m%d_%H%M%S)"
fi

# Get latest release info from Bitbucket Downloads API
if [ "$SDK_VERSION" = "latest" ]; then
    SDK_VERSION=$(curl -s -u "${BITBUCKET_USER}:${BITBUCKET_APP_PASSWORD}" \
        "https://api.bitbucket.org/2.0/repositories/${BITBUCKET_USER}/${BITBUCKET_REPO}/downloads" \
        | jq -r '.values[] | select(.name | contains("afero-sdk")) | .name' \
        | sort -V | tail -1 | grep -o 'v[0-9]\+\.[0-9]\+\.[0-9]\+[^.]*')
fi

# Download from Bitbucket Downloads
DOWNLOAD_URL="https://bitbucket.org/${BITBUCKET_USER}/${BITBUCKET_REPO}/downloads/afero-sdk-${SDK_VERSION}-community.tar.gz"

echo "Downloading from: $DOWNLOAD_URL"
wget --user="${BITBUCKET_USER}" --password="${BITBUCKET_APP_PASSWORD}" \
     -O "/tmp/afero-sdk-${SDK_VERSION}.tar.gz" \
     "$DOWNLOAD_URL"

mkdir -p "$LOCAL_SDK_PATH"
tar -xzf "/tmp/afero-sdk-${SDK_VERSION}.tar.gz" -C "$LOCAL_SDK_PATH" --strip-components=1

# Update device configuration to use new SDK
systemctl restart afero-device-service

echo "SDK updated successfully to version: $SDK_VERSION"
echo "Previous version backed up to: ${LOCAL_SDK_PATH}.backup.*"
