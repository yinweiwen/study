/**
 * Created by liu.xinyi
 * on 2016/3/31.
 */
'use strict';
import {Request, ApiTable} from '../../../utils/webapi';

export const INIT_AUTH = 'INIT_AUTH';
export function initAuth() {
    const user = JSON.parse(sessionStorage.getItem('user'));
    return {
        type: INIT_AUTH,
        payload: {
            user: user
        }
    };
}

export const REQUEST_LOGIN = 'REQUEST_LOGIN';
export const LOGIN_SUCCESS = 'LOGIN_SUCCESS';
export const LOGIN_ERROR = 'LOGIN_ERROR';
export function login(username, password) {
    return dispatch=> {
        dispatch({type: REQUEST_LOGIN});

        if (!username || !password) {
            dispatch({
                type: LOGIN_ERROR,
                payload: {error: '输入信息不完整'}
            });
            return Promise.resolve();
        }

        const url = ApiTable.login.replace('{username}', username).replace('{password}', password);

        return Request.post(url)
            .then(user=> {
                if (user.authorized) {
                    sessionStorage.setItem('user', JSON.stringify(user));
                    return dispatch({type: LOGIN_SUCCESS, payload: {user: user}});
                } else {
                    return dispatch({type: LOGIN_ERROR, payload: {error: '用户名或密码不正确'}});
                }
            })
            .catch(error=> dispatch({type: LOGIN_ERROR, payload: {error: '登录请求失败'}}));
    }
}

export const LOGOUT = 'LOGOUT';
export function logout() {
    const token = Cookie.getJSON('user').token;
    Cookie.remove('user');
    const url = ApiTable.logout.replace('{token}', token);
    Request.post(url);
    return {type: LOGOUT};
}

export default {
    initAuth,
    login,
    logout
}