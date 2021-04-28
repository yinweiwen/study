/**
 * Created by rain on 2016/1/25.
 */

'use strict';
/*jslint node:true*/
//from koa

const scaffold = require('fs-web-server-scaffold');
const config = require('./config.js');

module.exports = scaffold(config);