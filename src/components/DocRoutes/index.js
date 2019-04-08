import React from 'react';
import { Route, Redirect } from 'react-router-dom';

import DocRoot from '../DocRoot';
import DocSection from '../DocSection';

/* eslint import/no-webpack-loader-syntax: off */
let docsInstalling = require('!asciidoc-loader!../../docs/installing/index.adoc');
let docsSpring = require('!asciidoc-loader!../../docs/spring/index.adoc');
let docsConcepts = require('!asciidoc-loader!../../docs/concepts/index.adoc');
let docsJvm = require('!asciidoc-loader!../../docs/jvm/index.adoc');
let docsCache = require('!asciidoc-loader!../../docs/cache/index.adoc');
let docsOkHttpClient = require('!asciidoc-loader!../../docs/okhttpclient/index.adoc');
let docsConsoleReporter = require('!asciidoc-loader!../../docs/guide/console-reporter.adoc');
let docsHealthCheck = require('!asciidoc-loader!../../docs/guide/health-check.adoc');
let docsHttpSenderResilience4jRetry = require('!asciidoc-loader!../../docs/guide/http-sender-resilience4j-retry.adoc');
let docsSupport = require('!asciidoc-loader!../../docs/support/index.adoc');

const systems = ['appOptics', 'atlas', 'azure-monitor', 'datadog', 'dynatrace', 'elastic', 'ganglia', 'graphite', 'humio', 'influx', 'jmx', 'kairos', 'new-relic', 'prometheus', 'signalFx', 'statsD', 'wavefront'];

let docsBySystem = {};
systems.forEach(sys => docsBySystem[sys] = require(`!asciidoc-loader!../../docs/implementations/${sys}.adoc`));

export default function DocRoutes() {
  return (
    <div>
      <Route exact path="/docs" component={DocRoot} />

      <Route path="/docs/installing" render={() =>
        <DocSection title="Installing" content={docsInstalling}/>
      }/>

      <Route exact path="/docs/concepts" render={() =>
        <DocSection title="Concepts" content={docsConcepts} />} />

      <Route path="/docs/registry/:system" render={({ match }) => {
        let system = match.params.system;
        return systems.includes(system) ?
          <DocSection title={`Micrometer ${system.split('-').map(part => part[0].toUpperCase() + part.slice(1)).join(" ")}`}
                      content={docsBySystem[system]} /> :
          <Redirect to="/docs" />;
      }} />

      <Route path="/docs/ref/spring/1.5" render={() => <DocSection title="Spring Boot 1.5" content={docsSpring} />} />

      <Route path="/docs/ref/jvm" render={() =>
        <DocSection title="JVM and System Metrics" content={docsJvm}/>
      }/>

      <Route path="/docs/ref/cache" render={() =>
        <DocSection title="Cache Metrics" content={docsCache}/>
      }/>

      <Route path="/docs/ref/okhttpclient" render={() =>
        <DocSection title="OkHttpClient Metrics" content={docsOkHttpClient}/>
      }/>

      <Route path="/docs/guide/consoleReporter" render={() =>
        <DocSection title="Passing through to Dropwizard's console reporter" content={docsConsoleReporter}/>
      }/>

      <Route path="/docs/guide/healthAsGauge" render={() =>
        <DocSection title="Mapping Spring's health indicators to metrics" content={docsHealthCheck}/>
      }/>

      <Route path="/docs/guide/httpSenderResilience4jRetry" render={() =>
        <DocSection title="HttpSender with Resilience4j retry" content={docsHttpSenderResilience4jRetry}/>
      }/>

      <Route path="/docs/support" render={() =>
        <DocSection title="Micrometer Support Policy" content={docsSupport}/>
      }/>
    </div>
  )
}