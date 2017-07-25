import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter } from 'react-router-dom'
import App from './components/App/App';
import registerServiceWorker from './registerServiceWorker';
import { createBrowserHistory } from 'history';

import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css'; // where we wish to override bootstrap defaults

const history = createBrowserHistory();

history.listen((location) => {
  window.ga('set', 'page', location.pathname + location.search);
  window.ga('send', 'pageview');
});

ReactDOM.render(<BrowserRouter history={history}><App/></BrowserRouter>, document.getElementById('root'));
registerServiceWorker();
