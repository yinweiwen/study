/**
 * Created by liu.xinyi
 * on 2016/3/30.
 */
'use strict';

import request from 'superagent';
import jsonp from 'superagent-jsonp';

const rootUrl = '_api';

export const buildUrl = url => {
    const apiurl = `/${rootUrl}/${url}`;
    const user = JSON.parse(sessionStorage.getItem('user'));
    if (user == null) {
        return apiurl;
    }
    let connector = url.indexOf('?') === -1 ? '?' : '&';
    return `${apiurl}${connector}token=${user.token}`;
};

const resultHandler =
    (resolve, reject) =>
        (err, res) => {
            if (err) {
                if (err.status == 401) {
                    // 退出到登录页
                    const user = JSON.parse(sessionStorage.getItem('user'));
                    sessionStorage.removeItem('user');
                    if (user.domain) {
                        window.document.location.replace(`/${user.domain}/signin`)
                    } else {
                        window.document.location.replace('/signin');
                    }
                    reject('unauth');
                } else {
                    reject(err);
                }
            } else {
                resolve(res.body);
            }
        };

export const ApiTable = {
    login: 'user/login/{username}/{password}/info',
    logout: 'user/logout/{token}',
};

export class Request {
    static get = (url, query) => new Promise((resolve, reject) => {
        request.get(buildUrl(url)).query(query).end(resultHandler(resolve, reject));
    });

    static getJsonp = (url) => new Promise((resolve, reject) => {
        request.get(url).use(jsonp).end(resultHandler(resolve, reject));
    });

    static post = (url, data, query) => new Promise((resolve, reject) => {
        request.post(buildUrl(url)).query(query).send(data).end(resultHandler(resolve, reject));
    });

    static put = (url, data) => new Promise((resolve, reject) => {
        request.put(buildUrl(url)).send(data).end(resultHandler(resolve, reject));
    });

    static delete = url => new Promise((resolve, reject) => {
        request.delete(buildUrl(url)).end(resultHandler(resolve, reject));
    });
}