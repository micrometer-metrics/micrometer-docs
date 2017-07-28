import React from 'react';
import PropTypes from 'prop-types';
import Asciidoc from '../Asciidoc';
import './style.css';

/* eslint import/no-webpack-loader-syntax: off */
let index = require('!asciidoc-loader!../../docs/index.adoc');

export default function Docs({ system }) {
  return (
    <div className="container-fluid mt-4 ml-3 mr-3">
      <h1>User Manual for {system[0].toUpperCase() + system.slice(1)}</h1>
      <hr/>
      <Asciidoc
        source={index}
        attrs={{ system: system }} />
    </div>
  );
}

Docs.propTypes = {
  system: PropTypes.string.isRequired,
};