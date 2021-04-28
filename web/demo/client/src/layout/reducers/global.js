/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2016/1/13
 * Time: 20:03
 *
 */
'use strict';
import Immutable from 'immutable';
import {INIT_LAYOUT, RESIZE} from '../actions/global';

function global(state = { title: '', copyright: '', sections: [], actions: {}, plugins: {}, clientHeight: 768, clientWidth: 1024 }, action) {
    const payload = action.payload;
    switch (action.type) {
        case INIT_LAYOUT:
            return {
                title: payload.title,
                copyright: payload.copyright,
                sections: payload.sections,
                actions: payload.actions,
                clientHeight: state.clientHeight
            };
            break;
        case RESIZE:
            return Immutable.fromJS(state).merge({clientHeight: payload.clientHeight, clientWidth: payload.clientWidth}).toJS();
        default:
            return state;
    }
}

export default global;