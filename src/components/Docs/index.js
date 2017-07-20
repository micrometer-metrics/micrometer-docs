import React from 'react';
import { NavLink } from 'react-router-dom';

import './style.css';
import Manual from './Manual';

export default function Docs(props) {
  let system = props.location.pathname.split('/').slice(-1)[0];

  return (
    <div className="container-fluid mt-4 ml-3 mr-3">
      <h1>User Manual</h1>

      <p>
        Micrometer provides a simple facade over the instrumentation clients for the most popular monitoring systems,
        allowing you to instrument your JVM-based application code without vendor lock-in. Think SLF4J, but for
        application
        metrics!
      </p>

      <p>
        Micrometer is <i>not</i> a <a href="http://zipkin.io/">distributed tracing system</a> or a general
        purpose <a href="https://www.splunk.com/">event logger</a>. Application
        metrics recorded by Micrometer are intended to be used to observe, alert, and react to the current/recent
        operational
        state of your environment.
      </p>

      <p>
        Join the discussion with any comments and feature requests on Micrometer's
        public <a href="http://slack.micrometer.io">Slack team</a>.
      </p>

      <p>
        <strong>Select your monitoring system</strong> to tailor the manual to your specific use.
      </p>

      <div className="card-deck">
        <div className="card">
          <NavLink to="/docs/atlas" className={'system-card'} activeClassName={'active'}>
            <div className="card-block">
              <h4 className="card-title">Netflix Atlas</h4>
              <p className="card-text">Atlas is an in-memory dimensional time series
                database with built-in graphing, a custom stack-based query language,
                and advanced math operations.</p>
            </div>
          </NavLink>
        </div>
        <div className="card">
          <NavLink to="/docs/prometheus" className={'system-card'} activeClassName={'active'}>
            <div className="card-block">
              <h4 className="card-title">Prometheus</h4>
              <p className="card-text">Prometheus is an in-memory dimensional time series database
                with a simple built-in UI, a custom query language, and math operations.
                Prometheus is designed to operate on a pull model, scraping metrics from application
                instances periodically based on service discovery.
              </p>
            </div>
          </NavLink>
        </div>
        <div className="card">
          <NavLink to="/docs/datadog" className={'system-card'} activeClassName={'active'}>
            <div className="card-block">
              <h4 className="card-title">Datadog</h4>
              <p className="card-text">Datadog is a dimensional time-series SAAS with built-in
                dashboarding and alerting. Micrometer supports shipping metrics to Datadog directly
                via its API.</p>
            </div>
          </NavLink>
        </div>
      </div>

      <Manual system={system} />
    </div>
  );
}