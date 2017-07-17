import React from 'react';
import {Route} from 'react-router-dom';
import Navbar from '../Navbar/Navbar';
import Footer from '../Footer/Footer';
import Home from '../Home/Home';
import Docs from '../Docs/Docs';

export default function App() {
  return (
    <div>
      <Navbar/>

      <Route exact path="/" component={Home}/>
      <Route path="/docs" component={Docs}/>

      <div className="container-fluid" style={{paddingRight: 0, paddingLeft: 0}}>
        <Footer className="row"/>
      </div>
    </div>
  );
}

