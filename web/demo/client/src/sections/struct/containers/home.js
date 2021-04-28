/**
 * Created by PengLing on 2016/4/29.
 */
'use strict';
import React from 'react';
import {connect} from 'react-redux';

class Home extends React.Component {
    render() {
        return <div>{ this.props.children || <div>首页</div> }</div>;
    }
}

export default connect()(Home);