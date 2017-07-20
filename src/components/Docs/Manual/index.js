import React from 'react';
import PropTypes from 'prop-types';
import Asciidoc from '../Asciidoc';

/* eslint import/no-webpack-loader-syntax: off */

let index = require('!asciidoc-loader!../../../docs/index.adoc');

export default function Manual({system}) {
  return (
    <Asciidoc
      source={index}
      attrs={{system: system}}/>
  );
}

Manual.propTypes = {
  system: PropTypes.string.isRequired,
};