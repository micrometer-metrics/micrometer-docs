import {Alert, Button, Navbar} from "react-bootstrap";
import React from "react";
import './style.css';

export default function MigrationAlert() {
    return (
        <Navbar sticky="top" className="d-flex align-items-center justify-content-center text-center">
            <Alert variant="warning">
                <Alert.Heading>New Documentation Site</Alert.Heading>
                <Button variant="secondary" href="https://docs.micrometer.io/micrometer/reference/">Go to the new docs</Button>
                <br />
                This is an archive to keep links alive.
            </Alert>
        </Navbar>
    );
}
