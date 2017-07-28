import React from 'react';
import {Route, Redirect} from 'react-router-dom';
import Navbar from '../Navbar';
import Footer from '../Footer';
import Home from '../Home';
import DocsDeck from '../DocsDeck';
import Docs from '../Docs';

const systems = ['atlas', 'prometheus', 'datadog', 'graphite', 'ganglia', 'jmx'];

export default function App() {
  return (
    <div>
      <Navbar/>

      <Route exact path="/" component={Home}/>

      <Route exact path="/docs" component={DocsDeck}/>
      <Route path="/docs/:system" render={({ match }) =>
        systems.includes(match.params.system) ?
          <Docs system={match.params.system} /> :
          <Redirect to="/docs" />
      }/>

      <div className="container-fluid" style={{paddingRight: 0, paddingLeft: 0}}>
        <Footer className="row"/>
      </div>
    </div>
  );
}

