import React from 'react';
import { Route, Redirect, Switch } from 'react-router-dom';
import MyNavbar from '../MyNavbar';
import Footer from '../Footer';
import DocRoot from '../DocRoot';
import DocRoutes from '../DocRoutes';
import DocSection from "../DocSection";

/* eslint import/no-webpack-loader-syntax: off */
let docsSupport = require('!asciidoc-loader!../../generated-docs/support/index.adoc');

export default function App() {
  return (
    <div>
      <MyNavbar />

      <Switch>
        <Route exact path="/docs" component={DocRoot} />

        <Route exact path="/support" render={() =>
            <DocSection title="Micrometer Support Policy" content={docsSupport}/>
        }/>

        <Route exact path="/docs/support" render={() => <Redirect to="/support" />} />

        <Route path="/security-policy" component={() => { 
          window.location.href='https://tanzu.vmware.com/security';
          return null; 
        }
        } />

        <DocRoutes/>

        <Route path="**" render={() => <Redirect to="/docs" />} />
      </Switch>

      <div className="container-fluid" style={{ paddingRight: 0, paddingLeft: 0 }}>
        <Footer className="row" />
      </div>
    </div>
  );
}
