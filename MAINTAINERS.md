# Maintainers

Currently, active maintainers who may be found in the CNCF Slack.

- [Pierangelo Di Pilato](https://github.com/pierDipi)

# Maintainer's Guide

## Release Process

The release process is automated with Github actions. In order to perform a release:

1. Check if main CI pass.
1. Open the Github repository main page and go in the tab "Actions". Trigger the workflow "Bump version" and insert the new version to release. This will create a new release PR.
1. Check the release PR, merge it and cleanup the created branch.
1. Wait for the CI to complete the deploy of the modules to OSSRH.
1. Using the Github UI, create a new release, specifying the release notes and the tag to use.
1. Trigger again the workflow "Bump version" to bump versions back to a snapshot version.
1. Check the snapshot release PR, merge it and cleanup the created branch.

## Tips

Here are a few tips for repository maintainers.

* Stay on top of your pull requests. PRs that languish for too long can become difficult to merge.
* Work from your own fork. As you are making contributions to the project, you should be working from your own fork just as outside contributors do. This keeps the branches in github to a minimum and reduces unnecessary CI runs.
* Try to proactively label issues and pull requests with labels
* Actively review pull requests as they are submitted
* Triage issues once in a while in order to keep the repository alive.
  * If some issues are stale for too long because they are no longer valid/relevant or because the discussion reached no significant action items to perform, close them and invite the users to reopen if they need it.
  * If some PRs are no longer valid due to conflicts, but the PR is still needed, ask the contributor to rebase their PR.
  * If some issues and PRs are still relevant, use labels to help organize tasks.
  * If you find an issue that you want to create a pull request for, be sure to assign it to yourself so that other maintainers don't start working on it at the same time.

## Landing Pull Requests

When landing pull requests, be sure to check the first line uses an appropriate commit message prefix (e.g. docs, feat, lib, etc). If there is more than one commit, try to squash into a single commit. Usually this can just be done with the GitHub UI when merging the PR. Use "Squash and merge". To help ensure that everyone in the community has an opportunity to review and comment on pull requests, it's often good to have some time after a pull request has been submitted, and before it has landed. Some guidelines here about approvals and timing.

* No pull request may land without passing all automated checks
* All pull requests require at least one approval from a maintainer before landing
* A pull request author may approve their own PR, but will need an additional approval to land it
* If a maintainer has submitted a pull request and it has not received approval from at least one other maintainer, it can be landed after 72 hours
* If a pull request has both approvals and requested changes, it can't be landed until those requested changes are resolved

## Branch Management

The `main` branch is the bleeding edge. New major versions of the module
are cut from this branch and tagged. If you intend to submit a pull request
you should use `main HEAD` as your starting point.

Each major release will result in a new branch and tag. For example, the
release of version 1.0.0 of the module will result in a `v1.0.0` tag on the
release commit, and a new branch `v1.x.y` for subsequent minor and patch
level releases of that major version. However, development will continue
apace on `main` for the next major version - e.g. 2.0.0. Version branches
are only created for each major version. Minor and patch level releases
are simply tagged.
