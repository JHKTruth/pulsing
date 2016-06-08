import 'core-js/fn/object/assign';
import React from 'react';
import {render} from 'react-dom';
import {Router, Route, IndexRoute, hashHistory} from 'react-router';

import App from './components/App';
import TrendingPulseComponent from './components/pulsing/trending/TrendingPulseComponent';

// Render the app component into the dom
render((
  <Router history={hashHistory}>
    <Route path="/" component={App}>
      <IndexRoute component={TrendingPulseComponent} />
    </Route>
  </Router>
), document.getElementById('app'));