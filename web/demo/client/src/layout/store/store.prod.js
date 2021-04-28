/**
 * Created by liu.xinyi
 * on 2016/4/8.
 */
'use strict';

import { createStore, combineReducers, applyMiddleware } from 'redux';
import reduxThunk from 'redux-thunk';
import { routerReducer, routerMiddleware } from 'react-router-redux';
import innerReducers from '../reducers';

function configStore(reducers, history){
    const reducer = Object.assign({}, innerReducers, reducers, {
        routing: routerReducer
    });

    return createStore(combineReducers(reducer), {}, applyMiddleware(routerMiddleware(history), reduxThunk));
}

export default configStore;