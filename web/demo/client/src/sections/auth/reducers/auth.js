/**
 * Created by liu.xinyi
 * on 2016/3/31.
 */
'use strict';
import * as actionTypes from '../actions/auth';
import Immutable from 'immutable';

const initState = {
    user: null,
    isRequesting: false,
    error: null
};

function auth(state = initState, action) {
    const payload = action.payload;
    switch (action.type){
        case actionTypes.INIT_AUTH:
            return Immutable.fromJS(state).set('user', payload.user).toJS();
        case actionTypes.REQUEST_LOGIN:
            return Immutable.fromJS(state).merge({
                isRequesting: true,
                error: null
            }).toJS();
        case actionTypes.LOGIN_SUCCESS:
            return Immutable.fromJS(state).merge({
                isRequesting: false,
                user: payload.user
            }).toJS();
        case actionTypes.LOGIN_ERROR:
            return Immutable.fromJS(state).merge({
                isRequesting: false,
                error: payload.error
            }).toJS();
        case actionTypes.LOGOUT:
            return Immutable.fromJS(state).merge({
                user: null
            }).toJS();
        default:
            return state;
    }
}

export default auth;