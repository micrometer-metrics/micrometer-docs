import React from 'react';
import {Link} from 'react-router-dom';
import logo from '../../img/logo.svg';
import 'font-awesome/css/font-awesome.min.css';
import './style.css';
import { Nav, Navbar } from "react-bootstrap";

export default function MyNavbar() {
  return (
    <Navbar collapseOnSelect expand="lg" bg="dark" variant="dark" style={{ borderTop: '8px solid #1ba89c' }} >
      <Navbar.Brand as={Link} to="/" >
        <img src={logo} className="img-fluid" style={{ maxHeight: 80 }} alt="Micrometer" />
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="responsive-navbar-nav" />
      <Navbar.Collapse id="responsive-navbar-nav">
        <Nav className="ml-auto mt-2 mt-lg-0">
          <Nav.Item href="/docs">
            <Nav.Link as={Link} to="/docs"><i className="fa fa-lg fa-book"/> Documentation</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link href="https://github.com/micrometer-metrics/micrometer"><i className="fa fa-lg fa-github-alt" /> GitHub</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link href="https://twitter.com/micrometerio"><i className="fa fa-lg fa-twitter" /> Twitter</Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link href="https://slack.micrometer.io"><i className="fa fa-lg fa-slack" /> Slack</Nav.Link>
          </Nav.Item>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
}
