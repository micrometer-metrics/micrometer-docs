import React from 'react';
import ReactDOM from 'react-dom';
import { Router } from 'react-router-dom'
import App from './components/App/App';
import registerServiceWorker from './registerServiceWorker';
import { createBrowserHistory } from 'history';

// We have to import the CSS here. By placing it in index.html like the Bootstrap getting started guide suggests,
// Any CSS imported further down the component hierarchy will appear after Bootstrap CSS in the <style> ordering in <head>,
// effectively overriding Bootstrap styles.
import 'bootstrap/dist/css/bootstrap.min.css';

import './index.css'; // where we wish to override bootstrap defaults

const history = createBrowserHistory();

ReactDOM.render(<Router history={history}><App/></Router>, document.getElementById('root'));
registerServiceWorker();
