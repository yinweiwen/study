/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2016/1/13
 * Time: 11:59
 *
 */
'use strict';
import React from 'react';
import Immutable from 'immutable';
import moment from 'moment';
import 'moment/locale/zh-cn';
moment.locale('zh-cn');
import { render } from 'react-dom';
import { Provider } from 'react-redux';
import { Router, browserHistory } from 'react-router';
import { syncHistoryWithStore } from 'react-router-redux';
import { Layout, NoMatch } from './containers';
import configStore from './store';
import { initLayout } from './actions/global';

class Root extends React.Component {
    constructor(props) {
        super(props);
    }    

    render() {
        let routes = this.props.sections
            .reduce((p, c) => {
                return p.concat(c.routes);
            }, []);

        let innerRoutes = routes
            .filter(route => {
                return route.type === 'inner';
            })
            .map(r => {
                return r.route;
            });

        let homeRoutes = routes
            .filter(r=> {
                return r.type === 'home';
            }).map(r => {
                return r.route;
            });

        let combinedInnerRoutes = innerRoutes.concat(homeRoutes);

        let outerRoutes = routes
            .filter(s=> {
                return s.type === 'outer';
            }).map(r => {
                return r.route;
            });

        let homePage = homeRoutes[0].component;

        let rootRoute = {
            component: 'div',
            childRoutes: [
                {
                    path: '/',
                    component: Layout,
                    indexRoute: {component: homePage},
                    childRoutes: combinedInnerRoutes
                },
                ...outerRoutes,
                {
                    path: '*',
                    component: NoMatch
                }
            ]
        };

        let reducers = this.props.sections.reduce((p, c) => {
            return Object.assign(p, c.reducers);
        }, {});

        let actions = this.props.sections.reduce((p, c) => {
            let action = {};
            if(!c.key) console.warn('请给你的section添加一个key值，section name:' + c.name);
            action[c.key] = c.actions;
            return Object.assign(p, action);
        }, {});

        let store = configStore(reducers, browserHistory);

        const {sections, title, copyright} = this.props;
        store.dispatch(initLayout(title, copyright, sections, actions));
        store.dispatch(actions.auth.auth.initAuth());

        const history = syncHistoryWithStore(browserHistory, store);

        return (
            <Provider store={store}>
                <Router history={history} routes={rootRoute}/>
            </Provider>
        );
    }
}

export default Root;