## Micrometer website source (old) ![main branch status](https://github.com/micrometer-metrics/micrometer-docs/actions/workflows/ci.yml/badge.svg?branch=main)

This repo is the archive of the source code previously used for [micrometer.io](https://micrometer.io) and the corresponding docs now available at https://micrometer.io/docs.

The current website source is at https://github.com/micrometer-metrics/micrometer-website and documentation source is in each Micrometer repository so it is versioned with the codebase it is documenting.

Given the above, issues and pull requests should not be opened to this repository anymore. If you have an issue with the website, report it to https://github.com/micrometer-metrics/micrometer-website. If you have an issue or contribution for the documentation, please open it in the respective repository.

----

This project was bootstrapped with [Create React App](https://github.com/facebookincubator/create-react-app).

To check changes to this repository locally use `./gradlew yarnStart` (or `yarn start`) for development mode.

To run the build use `./gradlew build` (or `yarn build`). Note that you need to run `yarn` or `yarn install` first to install all the dependencies defined in `package.json`. To do that you can run `./gradlew installFrontend`

To upgrade project dependencies, run `./gradlew yarnUpgrade` (or `yarn upgrade`) which will update the `yarn.lock` file based on the defined version ranges and transitive dependencies from `package.json`.

To deploy the site to GitHub Pages, use the `./gradlew publish` (or `yarn deploy`) command.

To regenerate the adoc files just run `./gradlew generateCoalescedAdocs`.

-------------------------------------
_Licensed under [Apache Software License 2.0](https://www.apache.org/licenses/LICENSE-2.0)_

_Sponsored by [Broadcom](https://www.broadcom.com/)_
