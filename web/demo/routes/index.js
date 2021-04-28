/**
 * Created by liu.xinyi
 * on 2016/7/7.
 */
'use strict';
const path = require('path');
const fs = require('fs');

module.exports = {
    entry: function (app, router, opts) {
        fs.readdirSync(__dirname).forEach(function (dir) {
            if(fs.lstatSync(path.join(__dirname, dir)).isDirectory()){
                fs.readdirSync(path.join(__dirname, dir)).forEach(function (api) {
                    require(`./${dir}/${api}`).entry(app, router, opts);
                    app.fs.logger.log('info', '[Router]', 'Inject api:', dir + '/' + path.basename(api, '.js'));
                });
            }
        });
    }
};