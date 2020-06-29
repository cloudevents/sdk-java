# Release process

The release is automatically performed by Travis CI.

In order to trigger it, you can use the script `trigger_release.sh` like:

```bash
./scripts/trigger_release.sh --release 2.0.0-milestone2 --snapshot 2.1.0-SNAPSHOT --upstream origin
```

This script will:

* Perform a dump of the release using the release version
* Update all the *.md containing the release version
* Tag and commit all the above changes and eventually push them to the provided remote
* Perform a dump of the version back to {snapshotVersion}
* Commit all the above changes and eventually push them to the provided remote

After the script performed all the changes, you can create the new release on GitHub: https://github.com/cloudevents/sdk-java/releases/new

Note: Before running it pointing to upstream/master, try always in your local repo
