import React from 'react';
import {Link} from 'react-router-dom';
import logo from '../../img/logo.svg';
import 'font-awesome/css/font-awesome.min.css';
import './style.css';

export default function Navbar() {
  return (
    <div className="navbar navbar-toggleable-md navbar-inverse bg-inverse"
         style={{ borderTop: '8px solid #1ba89c' }}>
      <button className="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse"
              data-target="#navbarCollapse" aria-controls="navbarCollapse" aria-expanded="false"
              aria-label="Toggle navigation">
        <span className="navbar-toggler-icon" />
      </button>
      <Link to="/">
        <img src={logo} className="img-fluid" style={{ maxHeight: 80 }} alt="Micrometer" />
      </Link>
      <div className="collapse navbar-collapse" id="navbarCollapse">
        <ul className="navbar-nav ml-auto">
          <li className="nav-item active">
            <Link to="/docs" className="nav-link"><i className="fa fa-lg fa-book"/> Documentation</Link>
          </li>
          <li className="nav-item active">
            <a className="nav-link" href="https://github.com/micrometer-metrics/micrometer"><i className="fa fa-lg fa-github-alt" /> Github</a>
          </li>
          <li className="nav-item active">
            <a className="nav-link" href="http://slack.micrometer.io"><i className="fa fa-lg fa-slack" /> Slack</a>
          </li>
        </ul>
      </div>
    </div>
  );
}