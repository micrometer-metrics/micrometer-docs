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
            Vendor-neutral application observability facade
          </h1>
          <p className="lead" style={{ padding: 8, color: 'white', background: 'rgba(52, 48, 45, 0.8)' }}>Micrometer
            provides a simple facade over the instrumentation clients for the most popular observability systems,
            allowing you to instrument your JVM-based application code without vendor lock-in. Think SLF4J, but for
            observability.
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
            <p>Micrometer is the instrumentation library powering
              the delivery of application observability from Spring Boot applications.</p>
          </div>
        </div>
        <div className="row justify-content-center" style={{ padding: 30 }}>
          <div className="col-lg-6 col-md-12">
            <h2>Support for popular observability systems</h2>
            <p>As an instrumentation facade, Micrometer allows you to instrument your code with dimensional metrics, spans with
              a vendor-neutral interface and decide on the observability system as a last step. Instrumenting your core library
              code with Micrometer allows the libraries to be included in applications that ship data to different backends.
            </p>
            <p>
              Contains built-in support for <strong>AppOptics</strong>, <strong>Azure Monitor</strong>, Netflix <strong>Atlas</strong>, <strong>CloudWatch</strong>, <strong>Datadog</strong>, <strong>Dynatrace</strong>, <strong>Elastic</strong>, <strong>Ganglia</strong>, <strong>Graphite</strong>, <strong>Humio</strong>, <strong>Influx/Telegraf</strong>, <strong>JMX</strong>, <strong>KairosDB</strong>, <strong>New Relic</strong>, <strong>OpenTelemetry</strong> Protocol (OTLP), <strong>Prometheus</strong>, <strong>SignalFx</strong>, Google <strong>Stackdriver</strong>, <strong>StatsD</strong>, and <strong>Wavefront</strong>.
            </p>
            <p>
              Through <strong>Micrometer Observation</strong> and <strong>Micrometer Tracing</strong> you can ship your spans via <strong>OpenZipkin Brave</strong> or <strong>OpenTelemetry</strong> tracers to different backends (e.g. <strong>OpenZipkin</strong> or <strong>Wavefront</strong>).
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
