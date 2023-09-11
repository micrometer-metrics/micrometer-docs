import React from "react";
import { Link } from "react-router-dom";
import logo from "../../img/logo.svg";
import "font-awesome/css/font-awesome.min.css";
import "./style.css";
import { Nav, Navbar } from "react-bootstrap";
import NavDropdown from "react-bootstrap/NavDropdown";

export default function MyNavbar() {
  return (
    <Navbar
      collapseOnSelect
      expand="lg"
      bg="dark"
      variant="dark"
      style={{ borderTop: "8px solid #1ba89c" }}
    >
      <Navbar.Brand as={Link} to="/">
        <img
          src={logo}
          className="img-fluid"
          style={{ maxHeight: 80 }}
          alt="Micrometer"
        />
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="responsive-navbar-nav" />
      <Navbar.Collapse id="responsive-navbar-nav">
        <Nav className="ml-auto mt-2 mt-lg-0">
          <NavDropdown title="Documentation" menuVariant="dark">
            <NavDropdown.Item href="#action/3.1">
              Context Propagation
            </NavDropdown.Item>
            <NavDropdown.Item href="#action/3.2">Micrometer</NavDropdown.Item>
            <NavDropdown.Item href="#action/3.3">
              Micrometer Tracing
            </NavDropdown.Item>
            <NavDropdown.Item href="#action/3.4">
              Micrometer Docs Generator
            </NavDropdown.Item>
          </NavDropdown>
          {/* <Nav.Item href="/docs">
            <Nav.Link as={Link} to="/docs">
              <i className="fa fa-lg fa-book" /> Documentation
            </Nav.Link>
          </Nav.Item> */}
          <Nav.Item>
            <Nav.Link
              href="https://github.com/micrometer-metrics/micrometer"
              aria-label="GitHub"
            >
              <i className="fa fa-lg fa-github" /> GitHub
            </Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link
              href="https://twitter.com/micrometerio"
              aria-label="Twitter"
            >
              <i className="fa fa-lg fa-twitter" /> Twitter
            </Nav.Link>
          </Nav.Item>
          <Nav.Item>
            <Nav.Link href="https://slack.micrometer.io" aria-label="Slack">
              <i className="fa fa-lg fa-slack" /> Slack
            </Nav.Link>
          </Nav.Item>
          {/* <Nav.Item href="/docs">
            <Nav.Link as={Link} to="/docs"><i className="fa fa-lg fa-book"/> Documentation</Nav.Link>
          </Nav.Item>
           */}
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
}
