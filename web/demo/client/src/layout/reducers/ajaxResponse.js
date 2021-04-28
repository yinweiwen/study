/**
 * Created by liu.xinyi
 * on 2016/4/1.
 */
'use strict';
const initState = {
    msg: null
};

import Immutable from 'immutable';

/**
 * 全局ajax响应处理：
 * 判断action中是否有done字段，如果有，则修改store中的msg.done
 * 判断action中是否有error字段，如果有，则修改store中msg.error
 * 在layout中根据msg的值，呈现提示信息。
*/
export default function ajaxResponse(state = initState, action) {
    if (action.done) {
        return Immutable.fromJS(state).set('msg', {done: action.done}).toJS();
    }

    if (action.error) {
        return Immutable.fromJS(state).set('msg', {error: action.error}).toJS();
    }

    return {msg: null};
};