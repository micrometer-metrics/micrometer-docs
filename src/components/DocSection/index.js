import React from 'react';
import PropTypes from 'prop-types';
import Asciidoc from '../Asciidoc';

import './style.css';

export default function DocSection({ title, content, attrs }) {
  return (
    <div className="container-fluid mt-4 ml-3 mr-3">
      <h1>{title}</h1>
      <hr/>
      <Asciidoc
        source={content}
        attrs={attrs} />
    </div>
  );
}

DocSection.defaultProps = {
  attrs: {},
};

DocSection.propTypes = {
  title: PropTypes.string.isRequired,
  content: PropTypes.string.isRequired,
  attrs: PropTypes.object,
};