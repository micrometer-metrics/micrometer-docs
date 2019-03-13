import React, {Component} from 'react';
import PropTypes from 'prop-types';
import 'asciidoctor.js/dist/css/asciidoctor.css';

// To rebuild this with a small set of languages, clone highlight.js, run:
// `node tools/build.js java groovy http xml gradle yaml json -t node`
// Copy much of the build directory to ./highlight.
// You will have to force off a couple eslint rules in highlight.js as well.
import hljs from './highlight';
import './highlight.github.css';

let asciidoctor = require('asciidoctor.js')();

asciidoctor.Extensions.register(function () {
  this.treeProcessor(function() {
    let self = this;
    self.process(function (doc) {
      doc.findBy({'context': 'image'}).forEach(function(img) {
        // hack to add an additional attribute. otherwise, setAttribute only allows you to replace existing attributes
        let alt = img.getAttribute('alt');
        img.setAttribute('alt', alt + '" class="img-fluid', true);
      });
      return doc;
    });
  });
});

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