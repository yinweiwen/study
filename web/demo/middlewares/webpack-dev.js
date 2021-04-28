/**
 * Created by liu.xinyi
 * on 2016/7/1.
 */
'use strict';
const webpack = require('webpack');
const devConfig = require('../webpack.config');
const WebpackDevServer = require('webpack-dev-server');
const proxy = require('koa-proxy');
const convert = require('koa-convert');

const compile = webpack(devConfig);

module.exports = {
    entry: function (app, router, opts) {
        app.use(convert(proxy({
            host: 'http://localhost:5001',
            match: /\/build/,
            map: function (path) {
                return 'client' + path;
            }
        })));

        app.use(convert(proxy({
            host: 'http://localhost:5001',
            match: /\/$/,
            map: function (path) {
                return 'client/build/index.html';
            }
        })));

        var server = new WebpackDevServer(compile, {
            hot: true,
            quiet: false,
            noInfo: true,
            publicPath: "http://localhost:5001/client/build/",
            stats: { colors: true }
        });

        server.listen('5001', 'localhost', function (err) {
            if(err) {
                console.log(err);
            }
        })
    }
};