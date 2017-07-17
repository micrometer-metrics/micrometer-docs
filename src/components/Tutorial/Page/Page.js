import React, { Component } from 'react';
import CodeMirror from 'react-codemirror';
import { Pos } from 'codemirror';
import 'codemirror/mode/groovy/groovy.js';
import 'codemirror/addon/hint/show-hint.js';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/eclipse.css';

export default class Page extends Component {
  constructor(props) {
    super(props);

    this.state.code = {};
  }

  graph() {
    // TODO implement me
  }

  render() {
    return (
      <div className="container-fluid">
        <div className="row">
          <div className="col-6">
            <CodeMirror
              value={this.state.code}
              onChange={this.updateCode}
              options={{
                mode: 'groovy',
                lineNumbers: true,
                theme: 'eclipse',
                extraKeys: {
                  Tab: (cm) => cm.replaceSelection('  '),
                  'Alt-Enter': this.graph,
                },
              }} />
          </div>
          <div className="col-6">
            {/* the graph when ready to render */}
          </div>
        </div>
        <div className="row">
          <div className="col-12" style={{backgroundColor: '#ddd', border: '1px solid black'}}>
            {this.props.children}
          </div>
        </div>
      </div>
    );
  }
}