/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2015/12/24
 * Time: 16:52
 *
 */
'use strict';

import React from 'react';
import { render } from 'react-dom';
import App from './app';
import { LocaleProvider } from 'antd';
import zh_CN from 'antd/lib/locale-provider/zh_CN';

render((
    <LocaleProvider locale={zh_CN}>
        <App projectName="桥梁安全监测系统" />
    </LocaleProvider>
), document.getElementById('App'));
