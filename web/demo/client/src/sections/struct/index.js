/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2016/1/13
 * Time: 15:47
 *
 */
'use strict';

import reducers from './reducers';
import routes from './routes';
import actions from './actions';

export default {
    key: 'struct',
    name: '结构物',
    reducers: reducers,
    routes: routes,
    actions: actions
};