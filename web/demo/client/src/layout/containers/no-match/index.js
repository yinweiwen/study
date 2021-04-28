/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2016/1/13
 * Time: 14:29
 *
 */
'use strict';

import React from 'react';

class NoMatch extends React.Component {
    constructor(props) {
        super(props);
    }    

    render() {
        return (<div style={{ textAlign: 'center', padding: 120 }}>
            <p style={{ fontSize: 80, lineHeight: 1.5 }}>404</p>
            <p style={{ fontSize: 32, lineHeight: 2 }}>PAGE NOT FOUND</p>
            <p>很遗憾，您暂时无法访问该页面。</p>
            <p>请检查您访问的链接地址是否正确。</p>
            <p style={{ marginTop: 80 }}>Copyright © 2018 安信卓越</p>
        </div>);
    }
}

export default NoMatch;