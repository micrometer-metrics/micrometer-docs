## Micrometer website source ![main branch status](https://github.com/micrometer-metrics/micrometer-docs/actions/workflows/ci.yml/badge.svg?branch=main)

This repo is the source code for [micrometer.io](https://micrometer.io).

This project was bootstrapped with [Create React App](https://github.com/facebookincubator/create-react-app).

To check changes to this repository locally use `./gradlew yarnStart` (or `yarn start`) for development mode.

To run the build use `./gradlew build` (or `yarn build`). Note that you need to run `yarn` or `yarn install` first to install all the dependencies defined in `package.json`. To do that you can run `./gradlew installFrontend`

To upgrade project dependencies, run `./gradlew yarnUpgrade` (or `yarn upgrade`) which will update the `yarn.lock` file based on the defined version ranges and transitive dependencies from `package.json`.

To deploy the site to GitHub Pages, use the `./gradlew publish` (or `yarn deploy`) command.

To regenerate the adoc files just run `./gradlew generateCoalescedAdocs`.

-------------------------------------
_Licensed under [Apache Software License 2.0](https://www.apache.org/licenses/LICENSE-2.0)_

_Sponsored by [Broadcom](https://www.broadcom.com/)_
