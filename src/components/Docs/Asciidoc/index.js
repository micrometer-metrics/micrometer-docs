import React, {Component} from 'react';
import PropTypes from 'prop-types';
import 'asciidoctor.js/dist/css/asciidoctor.css';
import hljs from 'highlight.js';
import 'highlight.js/styles/github.css';

let asciidoctor = require('asciidoctor.js')();

export default class Asciidoc extends Component {
  constructor(props) {
    super(props);
    this.highlightCode = this.highlightCode.bind(this);
  }

  componentDidMount() {
    this.highlightCode();
  }

  componentDidUpdate() {
    this.highlightCode();
  }

  highlightCode() {
    this.root.querySelectorAll('pre code').forEach(node => hljs.highlightBlock(node));
  }

  render() {
    let converted = asciidoctor.convert(this.props.source, { attributes: this.props.attrs, safe: 'safe'});
    return (
      <div
        ref={(root) => { this.root = root; }}
        dangerouslySetInnerHTML={{ __html: converted }} />
    )
  }
}

Asciidoc.propTypes = {
  source: PropTypes.string,
  attrs: PropTypes.any,
};