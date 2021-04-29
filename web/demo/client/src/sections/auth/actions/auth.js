/**
 * Created by liu.xinyi
 * on 2016/3/31.
 */
 'use strict';

 import { Request, ApiTable } from '../../../utils/webapi';
 
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
     return dispatch => {
         dispatch({ type: REQUEST_LOGIN });
         if (username != 'admin' || password != '123456') {
             dispatch({
                 type: LOGIN_ERROR,
                 payload: { error: '用户名或密码不正确' }
             });
             return Promise.resolve();
         }
         const user = { authorized: true, username };
         sessionStorage.setItem('user', JSON.stringify(user));
         return dispatch({ type: LOGIN_SUCCESS, payload: { user } });
     }
 }
 
 export const LOGOUT = 'LOGOUT';
 export function logout() {
     sessionStorage.removeItem('user');
     return { type: LOGOUT };
 }
 
 export default {
     initAuth,
     login,
     logout
 }
 