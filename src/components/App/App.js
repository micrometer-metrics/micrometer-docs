import React from 'react';
import {Route, Redirect} from 'react-router-dom';
import Navbar from '../Navbar';
import Footer from '../Footer';
import Home from '../Home';
import Docs from '../Docs';

export default function App() {
  return (
    <div>
      <Navbar/>

      <Route exact path="/" component={Home}/>
      <Route exact path="/docs" render={() => <Redirect to="/docs/atlas" />}/>
      <Route path="/docs/prometheus" component={Docs} />
      <Route path="/docs/atlas" component={Docs} />
      <Route path="/docs/datadog" component={Docs} />

      <div className="container-fluid" style={{paddingRight: 0, paddingLeft: 0}}>
        <Footer className="row"/>
      </div>
    </div>
  );
}

