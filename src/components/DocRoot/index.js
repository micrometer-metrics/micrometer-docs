import React from 'react';
import { NavLink } from 'react-router-dom';

import './style.css';

export default function DocRoot() {
  return (
    <div className="container-fluid mt-4" style={{ paddingRight: 30, paddingLeft: 30 }}>
      <h1>Micrometer Documentation</h1>

      <p>
        Micrometer provides a simple facade over the instrumentation clients for the most popular monitoring systems,
        allowing you to instrument your JVM-based application code without vendor lock-in. Think SLF4J, but for
        application metrics! Application metrics recorded by Micrometer are intended to be used to observe, alert, and
        react to the current/recent operational state of your environment.
      </p>

      <p>
        Join the discussion with any comments and feature requests on Micrometer's
        public <a href="http://slack.micrometer.io"><i className="fa fa-lg fa-slack" /> Slack team</a>.
      </p>

      <ol>
        <li><NavLink className="doc-section" to="/docs/installing">Installing</NavLink>. Where to get the latest release
          and snapshot builds.
        </li>
        <li><NavLink className="doc-section" to="/docs/concepts">Concepts</NavLink>. An introduction to
          the abstraction provided by Micrometer.
        </li>
        <li><span className="doc-section">Setup</span>. Instructions for how to configure Micrometer for use with
          different monitoring systems. As a facade over multiple monitoring systems, the point of Micrometer is to
          allow you to instrument your code in the same way and be able to visualize the results in your monitoring
          system of choice.
          <ul>
            <li><NavLink className="doc-section" to="/docs/registry/atlas">Atlas</NavLink>. An
              in-memory dimensional time series database with built-in graphing, a custom stack-based query language,
              and advanced math operations. Atlas originated at Netflix, where it remains the operational metrics
              solution.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/datadog">Datadog</NavLink>. Datadog
              is a dimensional time-series SAAS with built-in dashboarding and alerting. Micrometer supports shipping
              metrics to Datadog directly via its API or through Dogstatsd via the StatsD registry.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/ganglia">Ganglia</NavLink>. An
              aging hierarchical metrics system which enjoyed wide popularity in Linux system monitoring and is still in
              place in many organizations. It originated in the early 2000s at the University of California, Berkeley.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/graphite">Graphite</NavLink>. One
              of the most popular current hierarchical metrics systems backed by a fixed-size database, similar in
              design and purpose to RRD. It originated at Orbitz in 2006 and was open sourced in 2008.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/influx">Influx</NavLink>.
              The InfluxData suite of tools supports real-time stream processing and storage of time-series data. It
              supports downsampling, automatically expiring and deleting unwanted data, as well as backup and restore.
              Analysis of data is done via a SQL-like query language.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/jmx">JMX</NavLink>. Micrometer
              provides a hierarchical mapping to JMX, primarily as a cheap and portable way to view metrics locally.
              Where JMX exporting is found in production, the same metrics are generally exported to another, more
              purpose-fit monitoring system.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/new-relic">New Relic</NavLink>.
              Micrometer publishes to New Relic Insights, a SaaS offering with a full UI and a query language called NRQL.
              New Relic Insights operates on a push model.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/prometheus">Prometheus</NavLink>.
              An in-memory dimensional time series database with a simple built-in UI, a custom query language, and math
              operations. Prometheus is designed to operate on a pull model, scraping metrics from application instances
              periodically based on service discovery.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/signalFx">SignalFx</NavLink>.
              SignalFx is a dimensional monitoring system SaaS with a full UI operating on a push model. It has a rich
              set of alert "detectors".
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/statsD">StatsD</NavLink>.
              Micrometer supports three flavors of StatsD: the original Etsy format plus the Datadog and Telegraf
              (Influx) extensions of StatsD that add dimensional support. Use this registry if you prefer to publish
              metrics to a StatsD agent.
            </li>

            <li><NavLink className="doc-section" to="/docs/registry/wavefront">Wavefront</NavLink>.
              Wavefront is a SaaS-based metrics monitoring and analytics platform that lets you visualize, query, and
              alert over data from across your entire stack (infrastructure, network, custom app metrics, business KPIs,
              etc.)
            </li>
          </ul>
        </li>
        <li><span className="doc-section">Reference</span>. Detailed list of out-of-the-box
          instrumentation provided by Micrometer.
          <ul>
            <li><span className="doc-section">Spring Boot</span>. As of Spring Boot 2.0.0.M5, Micrometer is the
              instrumentation library powering the delivery of application metrics from Spring. The <i>micrometer-spring-legacy</i>
              module provides drop-down support for Spring Boot 1.5.x.
              <ul>
                <li><NavLink className="doc-section" to="/docs/ref/spring/1.5">Spring Boot 1.5.x</NavLink></li>
                <li><a className="doc-section"
                       href="https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#production-ready-metrics">Spring
                  Boot 2.0</a></li>
              </ul>
            </li>
            <li><NavLink className="doc-section" to="/docs/ref/jvm">JVM</NavLink>. Metrics on classloaders, memory,
              garbage collection, threads, etc.
            </li>
            <li><NavLink className="doc-section" to="/docs/ref/cache">Cache</NavLink>. Instrumentation for the most
              popular caching frameworks.
            </li>
          </ul>
        </li>
        <li><span className="doc-section">Guides</span>. Examples demonstrating how to build actionable
          insights from metrics data.
          <ul>
            <li><NavLink className="doc-section" to="/docs/guide/consoleReporter">Passing through to Dropwizard's console reporter</NavLink></li>
          </ul>
        </li>
      </ol>
    </div>
  );
}