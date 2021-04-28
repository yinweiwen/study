/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2016/2/22
 * Time: 15:29
 *
 */
'use strict';

const views = require('koa-view');
const path = require('path');
const convert = require('koa-convert');
const co = require('co');

module.exports = {
    entry: function (app, router, opt) {
        app.use(convert(views(__dirname)));

        router.get('*', co.wrap(function* (ctx){
            yield ctx.render(path.join(__dirname, './index'));
        }));
    }
};