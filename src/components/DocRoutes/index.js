import React from 'react';
import { Route, Redirect } from 'react-router-dom';

import DocRoot from '../DocRoot';
import DocSection from '../DocSection';

/* eslint import/no-webpack-loader-syntax: off */
let docsInstalling = require('!asciidoc-loader!../../docs/installing/index.adoc');
let docsConcepts = require('!asciidoc-loader!../../docs/concepts/index.adoc');
let docsJvm = require('!asciidoc-loader!../../docs/jvm/index.adoc');
let docsCache = require('!asciidoc-loader!../../docs/cache/index.adoc');
let docsOkHttpClient = require('!asciidoc-loader!../../docs/okhttpclient/index.adoc');
let docsJetty = require('!asciidoc-loader!../../docs/jetty/index.adoc');
let docsConsoleReporter = require('!asciidoc-loader!../../docs/guide/console-reporter.adoc');
let docsHttpSenderResilience4jRetry = require('!asciidoc-loader!../../docs/guide/http-sender-resilience4j-retry.adoc');
let docsCustomMeterRegistry = require('!asciidoc-loader!../../docs/guide/custom-meter-registry.adoc');
let docsSupport = require('!asciidoc-loader!../../docs/support/index.adoc');
let docsTracing = require('!asciidoc-loader!../../generated-docs/tracing/index.adoc');
let docsContextPropagation = require('!asciidoc-loader!../../generated-docs/contextpropagation/index.adoc');

const systems = ['appOptics', 'atlas', 'azure-monitor', 'cloudwatch', 'datadog', 'dynatrace', 'elastic', 'ganglia', 'graphite', 'humio', 'influx', 'instana', 'jmx', 'kairos', 'new-relic', 'prometheus', 'signalFx', 'stackdriver', 'statsD', 'wavefront'];

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

      <Route path="/docs/ref/jvm" render={() =>
        <DocSection title="JVM and System Metrics" content={docsJvm}/>
      }/>

      <Route path="/docs/ref/cache" render={() =>
        <DocSection title="Cache Metrics" content={docsCache}/>
      }/>

      <Route path="/docs/ref/okhttpclient" render={() =>
        <DocSection title="OkHttpClient Metrics" content={docsOkHttpClient}/>
      }/>

      <Route path="/docs/ref/jetty" render={() =>
        <DocSection title="Jetty and Jersey Metrics" content={docsJetty}/>
      }/>

      <Route path="/docs/guide/consoleReporter" render={() =>
        <DocSection title="Passing through to Dropwizard's console reporter" content={docsConsoleReporter}/>
      }/>

      <Route path="/docs/guide/httpSenderResilience4jRetry" render={() =>
        <DocSection title="HttpSender with Resilience4j retry" content={docsHttpSenderResilience4jRetry}/>
      }/>

      <Route path="/docs/guide/customMeterRegistry" render={() =>
        <DocSection title="Custom meter registry" content={docsCustomMeterRegistry}/>
      }/>

      <Route path="/docs/support" render={() =>
        <DocSection title="Micrometer Support Policy" content={docsSupport}/>
      }/>

      <Route path="/docs/tracing" render={() =>
        <DocSection title="Tracing" content={docsTracing}/>
      }/>

      <Route path="/docs/contextPropagation" render={() =>
        <DocSection title="Context Propagation" content={docsContextPropagation}/>
      }/>
    </div>
  )
}
