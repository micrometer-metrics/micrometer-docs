import React from 'react';
import { NavLink } from 'react-router-dom';

export default function DocOptions() {
  return (
    <div className="container-fluid mt-4 ml-3 mr-3">
      <h1>User Manual</h1>

      <p>
        Micrometer provides a simple facade over the instrumentation clients for the most popular monitoring systems,
        allowing you to instrument your JVM-based application code without vendor lock-in. Think SLF4J, but for
        application metrics! Application metrics recorded by Micrometer are intended to be used to observe, alert, and
        react to the current/recent operational state of your environment.
      </p>

      <p>
        Micrometer is <i>not</i> a distributed tracing system or an event logger. Adrian Cole's talk on <a
        href="https://www.dotconferences.com/2017/04/adrian-cole-observability-3-ways-logging-metrics-tracing">"Observability
        3 Ways"</a> talk does a great job of highlighting the differences between these different types of systems.
      </p>

      <p>
        Join the discussion with any comments and feature requests on Micrometer's
        public <a href="http://slack.micrometer.io"><i className="fa fa-lg fa-slack"/> Slack team</a>.
      </p>

      <p>
        As a facade over multiple monitoring systems, the point of Micrometer is to allow you to instrument your code
        in the same way and be able to visualize the results in your monitoring system of choice. We've tailored the
        query and visualization aspects of the manual to each monitoring system. Select your monitoring system
        to view the manual for your specific use.
      </p>

      <dl>
        <dt><h3><NavLink to="/docs/atlas">Micrometer Atlas</NavLink></h3></dt>
        <dd>Atlas is an in-memory dimensional time series
          database with built-in graphing, a custom stack-based query language,
          and advanced math operations. Atlas originated at Netflix, where it remains
          the operational metrics solution.
        </dd>

        <dt><h3><NavLink to="/docs/prometheus">Micrometer Prometheus</NavLink></h3></dt>
        <dd>Prometheus is an in-memory dimensional time series database
          with a simple built-in UI, a custom query language, and math operations.
          Prometheus is designed to operate on a pull model, scraping metrics from application
          instances periodically based on service discovery.
        </dd>

        <dt><h3><NavLink to="/docs/datadog">Micrometer Datadog</NavLink></h3></dt>
        <dd>Datadog is a dimensional time-series SAAS with built-in
          dashboarding and alerting. Micrometer supports shipping metrics to Datadog directly
          via its API.
        </dd>

        <dt><h3><NavLink to="/docs/influx">Micrometer Influx</NavLink></h3></dt>
        <dd>The InfluxData suite of tools supports real-time stream processing and storage of time-series data. It supports downsampling, automatically expiring and deleting unwanted data, as well as backup and restore. Analysis of data is done via a SQL-like query language.
        </dd>

        <dt><h3><NavLink to="/docs/graphite">Micrometer Graphite</NavLink></h3></dt>
        <dd>Graphite is one of the most popular current hierarchical
          metrics systems backed by a fixed-size database, similar in design and purpose to
          RRD. It originated at Orbitz in 2006 and was open sourced in 2008.
        </dd>

        <dt><h3><NavLink to="/docs/ganglia">Micrometer Ganglia</NavLink></h3></dt>
        <dd>Ganglia is an aging hierarchical metrics system which
          enjoyed wide popularity in Linux system monitoring and is still in place
          in many organizations. It originated in the early 2000s at the University of California, Berkeley.
        </dd>

        <dt><h3><NavLink to="/docs/jmx">Micrometer JMX</NavLink></h3></dt>
        <dd>Micrometer provides a hierarchical mapping to JMX, primarily
          as a cheap and portable way to view metrics locally. Where JMX exporting is found in production,
          the same metrics are generally exported to another, more purpose-fit monitoring system.
        </dd>
      </dl>
    </div>
  );
}