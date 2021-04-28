/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2016/1/13
 * Time: 17:51
 *
 */
'use strict';

let store = null;
if(process.env.NODE_ENV == 'production'){
    store = require('./store.prod').default;
}else {
    store = require('./store.dev').default;
}

export default store;