/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2015/12/28
 * Time: 9:54
 *
 */
'use strict';

import React from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'react-redux';
import { push } from 'react-router-redux';
import { message, Row, Col, Layout, Menu, Icon } from 'antd';
import './antd.less';
import './styles.css';
import Sider from '../../components/sider';
import Header from '../../components/header';
import Footer from '../../components/footer';
import { resize } from '../../actions/global';
import * as NProgress from 'nprogress';
import Ps from 'perfect-scrollbar';

class LayoutContainer extends React.Component {
    constructor(props) {
        super(props);
        NProgress.configure({
            template: `<div class="bar" style="height:2px" role="bar"><div class="peg"></div></div><div class="spinner" role="spinner"><div class="spinner-icon"></div></div>`
        });
        this.headerHeight = 65;
        this.footerHeight = 32;
    }    

    componentWillMount() {
        NProgress.start();
    }

    componentDidMount() {
        NProgress.done();
        const extraHeight = this.headerHeight + this.footerHeight;
        this.props.dispatch(resize(document.body.clientHeight - extraHeight, document.body.clientWidth));
        window.onresize = () => {
            this.props.dispatch(resize(document.body.clientHeight - extraHeight, document.body.clientWidth));
        };

        const { user, dispatch } = this.props;
        if (!user || !user.authorized) {
            dispatch(push('/signin'));
            return;
        }
        Ps.initialize(document.getElementById('page-content'), { suppressScrollX: true });
    }

    componentWillReceiveProps(nextProps) {
        const { user, msg, dispatch } = nextProps;

        if (nextProps.location.pathname != this.props.location.pathname && nextProps.location.pathname != '/') {
            let foundRec = true;
            const pathname = nextProps.location.pathname.charAt(0) == '/' ? nextProps.location.pathname.substring(1) : nextProps.location.pathname;
            this.props.sections.forEach(s => {
                if (s.checkRoute) {
                    foundRec = foundRec && s.checkRoute(pathname, this.props.user);
                }
            });
            if (!foundRec) {
                message.error('无此模块权限');
                dispatch(push('/'));
            }

            NProgress.start();
        }

        if (!user || !user.authorized) {
            dispatch(push('/signin'));
        }

        if (msg) {
            message.destroy();
            if (msg.done) {
                message.success(msg.done);
            }

            if (msg.error) {
                message.error(msg.error);
            }
        }
    }

    componentDidUpdate() {
        NProgress.done();

        const dom = document.getElementById('page-content');
        Ps.update(dom);
        dom.scrollTop = 0;
    }

    logout = () => {
        const { actions, user } = this.props;

        this.props.dispatch(actions.auth.auth.logout(user));
        this.props.dispatch(push(`/${user.domain}/signin`));
    }

    render() {
        const { user, title, plugins, copyright, children, sections, dispatch, clientWidth, clientHeight, location, userInfo } = this.props;

        if (!user || !user.authorized) {
            return <div />;
        }
        const newSections = []
        if (this.props.user.resources.indexOf("013") < 0 && this.props.user.resources.indexOf("014") < 0) {
            sections.map(s => {
                return s.key != "management" ? newSections.push(s) : ''
            })
        }
        const headerProps = { style: { height: this.headerHeight, background: '#fff', padding: 0 } };
        const footerProps = { style: { height: `${this.footerHeight}px`, lineHeight: `${this.footerHeight}px`, textAlign: 'center', padding: 0 } };

        return (
            <Layout id="layout">
                <Layout.Sider width="64">
                    <div className="logo" />
                    <Sider sections={newSections.length == 0 ? sections : newSections} dispatch={dispatch} user={user} pathname={location.pathname} />
                </Layout.Sider>
                <Layout>
                    <Layout.Header {...headerProps}>
                        <Header title={user.orgName} user={user} plugins={plugins} pathname={location.pathname} notice={this.props.notice} logout={this.logout} userInfo={userInfo} />
                    </Layout.Header>
                    <Layout.Content id="page-content" style={{ position: 'relative', margin: '16px 4px 0px 16px', height: clientHeight - 16 }}>
                        <div style={{ paddingRight: 16 }}>
                            {children}
                        </div>
                    </Layout.Content>
                    <Layout.Footer {...footerProps}>
                        {copyright}
                    </Layout.Footer>
                </Layout>
            </Layout>
        );
    }
}

function mapStateToProps(state) {
    const { global, auth, ajaxResponse } = state;

    return {
        title: global.title,
        copyright: global.copyright,
        sections: global.sections,
        actions: global.actions,
        clientWidth: global.clientWidth,
        clientHeight: global.clientHeight,
        msg: ajaxResponse.msg,
        user: auth.user
    };
}

export default connect(mapStateToProps)(LayoutContainer);