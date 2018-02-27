import React from 'react';
import logoNoTitle from '../../img/logo-no-title.svg';
import background from '../../img/playback-latency.png';

export default function Home() {
  return (
    <div>
      <div className="jumbotron text-center" style={{ background: `url(${background}) no-repeat center center` }}>
        <div className="container">
          <img src={logoNoTitle} className="img-fluid" alt="" />
          <h1 className="jumbotron-heading mt-3" style={{ color: 'white', background: 'rgba(52, 48, 45, 0.8)' }}>
            Vendor-neutral application metrics facade
          </h1>
          <p className="lead" style={{ padding: 8, color: 'white', background: 'rgba(52, 48, 45, 0.8)' }}>Micrometer
            provides a simple facade over the instrumentation clients for the most popular monitoring systems,
            allowing you to instrument your JVM-based application code without vendor lock-in. Think SLF4J, but for
            metrics.
          </p>
        </div>
      </div>
      <div className="container-fluid">
        <div className="row" style={{ background: 'rgba(17, 122, 113, 0.8)', color: 'white', padding: 30 }}>
          <div className="col-lg-4 text-center">
            <i className="fa fa-4x fa-database" aria-hidden="true" />
            <h2>Dimensional Metrics</h2>
            <p>Micrometer provides vendor-neutral interfaces for <strong>timers</strong>, <strong>gauges</strong>, <strong>counters</strong>, <strong>distribution summaries</strong>, and <strong>long task timers</strong> with a dimensional data model that, when paired with a dimensional monitoring system, allows for efficient access to a particular named metric with the ability to drill down across its dimensions.</p>
          </div>
          <div className="col-lg-4 text-center">
            <i className="fa fa-4x fa-area-chart" aria-hidden="true" />
            <h2>Pre-configured Bindings</h2>
            <p>Out-of-the-box instrumentation of caches, the class loader, garbage collection, processor utilization,
              thread pools, and more tailored to actionable insight.
            </p>
          </div>
          <div className="col-lg-4 text-center">
            <i className="fa fa-4x fa-leaf" aria-hidden="true" />
            <h2>Integrated into Spring</h2>
            <p>Starting with Spring Boot 2.0, Micrometer is the instrumentation library powering
              the delivery of application metrics from Spring. Support is ported back to Boot 1.x
              through an additional library dependency.</p>
          </div>
        </div>
        <div className="row justify-content-center" style={{ padding: 30 }}>
          <div className="col-lg-6 col-md-12">
            <h2>Support for popular monitoring systems</h2>
            <p>As an instrumentation facade, Micrometer allows you to instrument your code with dimensional metrics with
              a
              vendor-neutral interface and decide on the monitoring system as a last step. Instrumenting your core
              library
              code with Micrometer allows the libraries to be included in applications that ship metrics to different
              backends.</p>
            <p>
              Contains built-in support for <strong>Prometheus</strong>, Netflix <strong>Atlas</strong>, <strong>CloudWatch</strong>, <strong>Datadog</strong>, <strong>Graphite</strong>, <strong>Ganglia</strong>, <strong>JMX</strong>, <strong>Influx/Telegraf</strong>, <strong>New Relic</strong>, <strong>StatsD</strong>, <strong>SignalFx</strong>, and <strong>Wavefront</strong>.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}