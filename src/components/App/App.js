import React from 'react';
import { Route, Redirect, Switch } from 'react-router-dom';
import MyNavbar from '../MyNavbar';
import Footer from '../Footer';
import DocRoot from '../DocRoot';
import DocRoutes from '../DocRoutes';
import MigrationAlert from "../MigrationAlert";

export default function App() {
  return (
    <div>
      <MyNavbar />

      <MigrationAlert />

      <Switch>
        <Route exact path="/docs" component={DocRoot} />

          <Route exact path="/docs/support" component={() => {
              window.location.replace('/support');
              return null;
          }
          }/>

        <DocRoutes/>

        <Route path="**" render={() => <Redirect to="/docs" />} />
      </Switch>

      <div className="container-fluid" style={{ paddingRight: 0, paddingLeft: 0 }}>
        <Footer className="row" />
      </div>
    </div>
  );
}
