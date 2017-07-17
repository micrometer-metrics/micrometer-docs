import React from 'react';

import Md from '../Markdown';

/* eslint import/no-webpack-loader-syntax: off */
import QuickStart from 'raw-loader!./Sections/QuickStart.md';

export default function Docs() {
  console.log(QuickStart);

  return (
    <div className="container-fluid">
      <h1>Reference Documentation</h1>

      <div className="row">
        <div className="col-8 card-deck">
          <div className="card">
            <div className="card-block">
              <h4 className="card-title">Netflix Atlas</h4>
              <p className="card-text">Atlas was developed by Netflix to manage dimensional time series data for near
                real-time operational insight. Atlas features in-memory data storage, allowing it to gather and report
                very large numbers of metrics, very quickly.</p>
            </div>
          </div>
          <div className="card">
            <div className="card-block">
              <h4 className="card-title">Prometheus</h4>
              <p className="card-text">This card has supporting text below as a natural lead-in to additional
                content.</p>
            </div>
          </div>
          <div className="card">
            <div className="card-block">
              <h4 className="card-title">Datadog</h4>
              <p className="card-text">This is a wider card with supporting text below as a natural lead-in to
                additional content. This card has even longer content than the first to show that equal height
                action.</p>
            </div>
          </div>
        </div>
      </div>

      <div className="row">
        <div className="col-12">
          <Md className="mt-3" source={QuickStart} />
        </div>
      </div>
    </div>
  );
}