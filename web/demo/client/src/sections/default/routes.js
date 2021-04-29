'use strict';

import { Home } from './containers';

export default [{
    type: 'home',
    route: {
        path: '/',
        component: Home,
        childRoutes: []
    }
}];