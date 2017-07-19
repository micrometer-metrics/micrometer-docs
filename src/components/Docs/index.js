import React from 'react';

import './Asciidoc/style.css';
import Asciidoc from 'console-loader!./Asciidoc';

/* eslint import/no-webpack-loader-syntax: off */

let index = require('!asciidoc-loader!../../docs/index.adoc');

export default function Docs() {
  return (
    <div className="container-fluid mt-3">
      <div className="row mt-3">
        <div className="col-12">
          <Asciidoc
            source={index}
            attrs={{system: 'atlas'}}/>
        </div>
      </div>
    </div>
  );
}