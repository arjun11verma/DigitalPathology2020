import React from 'react';
import {BrowserRouter, Switch, Route} from 'react-router-dom'

import Homepage from './Homepage'
import LoginPage from './LoginPage';

function App() {
  return (
    <BrowserRouter>
      <Switch>
        <Route exact path = "/" component = {LoginPage}/>
        <Route exact path = "/Homepage/:id" component = {Homepage}/>
      </Switch>
    </BrowserRouter>
    );
}

export default App;
