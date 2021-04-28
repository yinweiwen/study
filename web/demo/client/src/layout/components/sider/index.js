import React, { Component } from 'react';
import {Menu, Icon} from 'antd';

class Sider extends Component {    
    constructor(props) {
        super(props);
    }

    render() {
        const { sections, dispatch, user, pathname } = this.props;

        let items = sections.filter(s => typeof s.getNavItem == 'function')
            .reduce((p, c) => {
                let s = c.getNavItem(user, dispatch);
                if (s != null) {
                    if (Array.isArray(s)) {
                        p = p.concat(s);
                    } else {
                        p.push(s);
                    }
                }
                return p;
            }, []);

        let current = pathname;        
        if(pathname == '/' || pathname == '') {
            current = 'default';
        } else if(pathname.charAt(0) == '/'){
            current = pathname.substring(1);
        }

        if(current.indexOf('/') != -1){
            current = current.substring(0, current.indexOf('/'));
        }

        return (
            <Menu id="sider" theme="dark" mode={'vertical'} defaultSelectedKeys={['default']} selectedKeys={[current]}>                
                {items}
            </Menu>
        )
    }
}

export default Sider;