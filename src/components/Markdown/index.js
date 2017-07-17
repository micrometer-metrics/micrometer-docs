import React from 'react';
import PropTypes from 'prop-types';
import ReactMarkdown from 'react-remarkable';
import hljs from 'highlight.js';
import format from 'string-template';

export default function Markdown({ source, templates }) {
  return (
    <ReactMarkdown
      options={{
        highlight(str, lang) {
          if (lang && hljs.getLanguage(lang)) {
            try {
              return hljs.highlight(lang, str).value;
            } catch (err) { console.log(err); }
          }

          try {
            return hljs.highlightAuto(str).value;
          } catch (err) { console.log(err); }

          return ''; // use external default escaping
        },
        langPrefix: 'hljs language-',
      }}
    >
      {format(source, templates)}
    </ReactMarkdown>
  );
}

Markdown.defaultProps = {
  source: '',
  templates: {},
};

Markdown.propTypes = {
  source: PropTypes.any,
  templates: PropTypes.object,
};
