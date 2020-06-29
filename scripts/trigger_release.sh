#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function die() { echo "$*" 1>&2 ; exit 1; }

# Example usage
# ./scripts/trigger_release.sh --branch upstream/master --release 2.0.0 --snapshot 2.1.0-SNAPSHOT

# In order to start the release the script:
# * Performs a dump of the release using the release version prompted on GH using mvn versions:set -DnewVersion={newVersion}
# * Updates all the *.md containing the release version
# * Commits straight on master the above changes
# * Performs a dump of the release back to {snapshotVersion}
# * Commits straight on master the above changes

REMOTE_BRANCH=""
NEW_SNAPSHOT=""
NEW_VERSION=""

# Loop through arguments and process them
while (( "$#" )); do
    case $1 in
        -b|--branch)
            if [[ -n $2 ]]; then
                REMOTE_BRANCH=$2
                shift
            else
                die 'ERROR: "--branch" requires a non-empty option argument.'
            fi
        ;;
        -r|--release)
            if [[ -n $2 ]]; then
                NEW_VERSION=$2
                shift
            else
                die 'ERROR: "--version" requires a non-empty option argument.'
            fi
        ;;
        -s|--snapshot)
            if [[ -n $2 ]]; then
                NEW_SNAPSHOT=$2
                shift
            else
                die 'ERROR: "--snapshot" requires a non-empty option argument.'
            fi
        ;;
    esac
    shift
done

if [ -z "$REMOTE_BRANCH" ]; then
    echo "Remote branch is not specified, I'm gonna perform the changes only locally"
else
    echo "Going to release on branch $REMOTE_BRANCH"
fi

if [ -z "$NEW_VERSION" ]; then
    die 'ERROR: version is not specified'
fi

if [ -z "$NEW_SNAPSHOT" ]; then
    die 'ERROR: new snapshot is not specified'
fi

echo "Dumping to release $NEW_VERSION"

mvn versions:set -DnewVersion="$NEW_VERSION"
sed -i -e 's+<version>[a-zA-Z0-9.-]*<\/version>+<version>2.0.0-milestone2</version>+g' **/*.md

git add **/*.md
git add **/pom.xml
git commit --signoff -m "Release $NEW_VERSION"
git tag $NEW_VERSION

if [ -n "$REMOTE_BRANCH" ]; then
    git push -u $REMOTE_BRANCH
fi

echo "Dumping to snapshot $NEW_SNAPSHOT"

mvn versions:set -DnewVersion="$NEW_SNAPSHOT"

git add **/pom.xml
git commit --signoff -m "Release $NEW_SNAPSHOT"

if [ -n "$REMOTE_BRANCH" ]; then
    git push -u $REMOTE_BRANCH
fi
