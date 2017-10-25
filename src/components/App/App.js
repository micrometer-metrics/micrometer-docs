import React from 'react';
import { Route, Redirect, Switch } from 'react-router-dom';
import Navbar from '../Navbar';
import Footer from '../Footer';
import Home from '../Home';
import DocRoutes from '../DocRoutes';

export default function App() {
  return (
    <div>
      <Navbar />

      <Switch>
        <Route exact path="/" component={Home} />

        <DocRoutes/>

        <Route path="**" render={() => <Redirect to="/" />} />
      </Switch>

      <div className="container-fluid" style={{ paddingRight: 0, paddingLeft: 0 }}>
        <Footer className="row" />
      </div>
    </div>
  );
}
