#!/usr/bin/env bash
if [[ "$TRAVIS_PULL_REQUEST" != "false" ]]; then
	echo "This is a pull request. No HockeyApp deployment will be done."
	exit 0
fi

if [[ "$TRAVIS_BRANCH" = "develop" ]]; then
    echo "Build on branch develop. Pushing to HockeyApp."
    if [ -z "$HOCKEY_APP_TOKEN" ]; then
        echo "ERROR - HockeyApp Token not set."
        exit 1
    fi
    curl \
    -F "status=2" \
    -F "notify=0" \
    -F "ipa=@app/build/outputs/apk/app-release.apk" \
    -H "X-HockeyAppToken: $HOCKEY_APP_TOKEN" \
    https://rink.hockeyapp.net/api/2/apps/upload
fi

if [[ "$TRAVIS_BRANCH" = "master" ]]; then
    echo "Build on branch master. Pushing to AWS."
    echo "ERROR - Push to AWS not implemented!"
fi