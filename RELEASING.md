# Release Process

The release process is automated with Github actions. In order to perform a release:

1. Check if main CI pass.
1. Open the Github repository main page and go in the tab "Actions". Trigger the workflow "Bump version" and insert the new version to release. This will create a new release PR.
1. Check the release PR, merge it and cleanup the created branch.
1. Wait for the CI to complete the deploy of the modules to OSSRH.
1. Using the Github UI, create a new release, specifying the release notes and the tag to use.
1. Trigger again the workflow "Bump version" to bump versions back to a snapshot version.
1. Check the snapshot release PR, merge it and cleanup the created branch.

