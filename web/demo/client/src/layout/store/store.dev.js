/**
 * Created by liu.xinyi
 * on 2016/4/8.
 */
'use strict';
import { createStore, combineReducers, applyMiddleware, compose } from 'redux';
import reduxThunk from 'redux-thunk';
import { routerReducer, routerMiddleware } from 'react-router-redux';
import innerReducers from '../reducers';

function configStore(reducers, history){
    const reducer = Object.assign({}, innerReducers, reducers, {
        routing: routerReducer
    });

    const enhancers = compose(
        applyMiddleware(routerMiddleware(history), reduxThunk),
        window.devToolsExtension ? window.devToolsExtension() : f => f
    );

    return createStore(combineReducers(reducer), {}, enhancers);
}

export default configStore;