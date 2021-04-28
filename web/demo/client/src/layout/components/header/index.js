/**
 * Created by liu.xinyi
 * on 2016/3/31.
 */
'use strict';
import React from 'react';
import { Menu, Icon, Badge, Row, Col } from 'antd';
import { Link } from 'react-router';

export default class Header extends React.Component {
    constructor(props) {
        super(props);
    }
    
    render() {
        const { pathname, user, userInfo } = this.props;

        let current = pathname;
        if (pathname == '/' || pathname == '') {
            current = 'default';
        } else if (pathname.charAt(0) == '/') {
            current = pathname.substring(1);
        }

        if (current.indexOf('/') != -1) {
            current = current.substring(0, current.indexOf('/'));
        }

        return (
            <div>
                <div style={{ float: 'left', paddingLeft: 24, fontSize: 16 }}>{this.props.title}</div>
                <div id="nav" style={{ float: 'right', marginRight: 24 }}>
                    <Menu mode='horizontal' selectedKeys={[current]}
                        onClick={item => { item.key == 'logout' && this.props.logout() }} >
                        <Menu.Item key="notification">
                            <Link to="/notification/systemNotice" style={{ color: '#666', fontSize: 16 }}>
                                <Badge count={this.props.notice.unread ? this.props.notice.unread.length : 0}>
                                    <Icon type="mail" />
                                </Badge>
                            </Link>
                        </Menu.Item>
                        <Menu.Item key="help">
                            <span><Icon type="question-circle-o" />����</span>
                        </Menu.Item>
                        <Menu.SubMenu key="user" title={
                            <div>
                                <div style={{ float: 'left', width: 36, height: 36, margin: '14px 8px' }}><img style={{ width: 36, height: 36 }} src={`/assets/avatar/${userInfo.avator}`} /></div>
                                <div style={{ float: 'left' }}>{userInfo.name}</div>
                            </div>}>
                            <Menu.Item key="profile">
                                <Link to="/profile"><Icon type="user" />��������</Link>
                            </Menu.Item>
                            <Menu.Item key="logout">
                                <span><Icon type="logout" />�˳�</span>
                            </Menu.Item>
                        </Menu.SubMenu>
                    </Menu>
                </div>
            </div>
        );
    }
};