/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2015/12/29
 * Time: 16:14
 *
 */
'use strict';

import routes from './routes';
import reducers from './reducers';
import actions from './actions';
import {getNavItem} from './containers/nav-item';

export default {
    key: 'auth',        
    reducers: reducers,
    routes: routes,
    actions: actions,
    getNavItem: getNavItem
};