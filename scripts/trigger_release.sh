#!/usr/bin/env bash

set -o errexit
set -o nounset
set -o pipefail

function die() { echo "$*" 1>&2 ; exit 1; }

# Example usage
# ./scripts/trigger_release.sh --upstream upstream --release 2.0.0 --snapshot 2.1.0-SNAPSHOT

# In order to start the release the script:
# * Performs a dump of the release using the release version
# * Updates all the *.md containing the release version
# * Commits all the above changes and eventually push them to the provided remote
# * Performs a dump of the version back to {snapshotVersion}
# * Commits all the above changes and eventually push them to the provided remote

THIS_BRANCH=$(git rev-parse --abbrev-ref HEAD)
REMOTE=""
NEW_SNAPSHOT=""
NEW_VERSION=""

# Loop through arguments and process them
while (( "$#" )); do
    case $1 in
        -u|--upstream)
            if [[ -n $2 ]]; then
                REMOTE=$2
                shift
            else
                die 'ERROR: "--upstream" requires a non-empty option argument.'
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

if [ -z "$REMOTE" ]; then
    echo "Remote is not specified, I'm gonna perform the changes only locally"
else
    echo "Going to release on remote $REMOTE"
fi

if [ -z "$NEW_VERSION" ]; then
    die 'ERROR: version is not specified'
fi

if [ -z "$NEW_SNAPSHOT" ]; then
    die 'ERROR: new snapshot is not specified'
fi

echo "Dumping to release $NEW_VERSION"

mvn versions:set -DnewVersion="$NEW_VERSION"
find . -name "pom.xml" -exec git add {} \;
sed -i -e 's+<version>[a-zA-Z0-9.-]*<\/version>+<version>2.0.0-milestone2</version>+g' ***/*.md
find . -name "*.md" -exec git add {} \;

git commit --signoff -m "Release $NEW_VERSION"
git tag $NEW_VERSION

if [ -n "$REMOTE" ]; then
    git push -u $REMOTE $THIS_BRANCH
fi

echo "Dumping to snapshot $NEW_SNAPSHOT"

mvn versions:set -DnewVersion="$NEW_SNAPSHOT"
find . -name "pom.xml" -exec git add {} \;

git commit --signoff -m "Release $NEW_SNAPSHOT"

if [ -n "$REMOTE" ]; then
    git push -u $REMOTE $THIS_BRANCH
fi

echo "Done! Now you can create the release on GitHub!"
