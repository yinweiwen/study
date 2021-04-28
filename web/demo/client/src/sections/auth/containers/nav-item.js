/**
 * Created by liu.xinyi
 * on 2016/7/4.
 */
'use strict';
import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Menu, Icon} from 'antd';
import {user} from './style.css';
import {logout} from '../actions/auth';

class NavItem extends Component{
    render() {
        if (!this.props.user) {
            return <span />
        }

        return (
            <Menu.SubMenu title={<span>{this.props.user.userName}</span>} {...this.props}>
                <Menu.Item key="logout" className="menu-dp-item">
                    <div onClick={()=>{this.props.dispatch(logout())}} className={user}>退出</div>
                </Menu.Item>
            </Menu.SubMenu>);
    }
}

const NavItemContainer = connect()(NavItem);

export function getNavItem(user) {
    return <NavItemContainer user={user} />
}

