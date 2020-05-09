#!/bin/bash

#
# Used in GitHub Actions
# to upload the APK to Firebase Storage
# after its compilation
# allowing everyone to download it
#

ACCESS_TOKEN=`curl \
    --fail \
    -F "client_id=${OAUTH_CLIENT_ID}" \
    -F "client_secret=${OAUTH_CLIENT_SECRET}" \
    -F "grant_type=refresh_token" \
    -F "refresh_token=${OAUTH_REFRESH_TOKEN}" \
    "https://oauth2.googleapis.com/token" \
    2> /dev/null \
    | jq -r .access_token`

curl \
    --fail \
    -X POST \
    --data-binary "@${SIGNED_RELEASE_FILE}" \
    -H "Authorization: Bearer $ACCESS_TOKEN" \
    -H "Content-Type: application/vnd.android.package-archive" \
    "https://storage.googleapis.com/upload/storage/v1/b/${BUCKET_NAME}/o?uploadType=media&name=${APK_STORAGE_NAME}" \
    >/dev/null 2>/dev/null
