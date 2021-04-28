/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2016/1/13
 * Time: 13:57
 *
 */
'use strict';

import React from 'react';
import {connect} from 'react-redux';
import {login} from './actions/auth';
import { Login } from './containers';

export default[
    {type: 'outer', route: {path:"signin", component: Login }}        
];