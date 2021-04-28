/**
 * Created by liu.xinyi
 * on 2016/3/30.
 */
'use strict';
import {Home} from './containers';

export default [{
    type: 'home',
    route: {
        path: '/',
        component: Home,
        childRoutes: [                        
        ]
    }
}];