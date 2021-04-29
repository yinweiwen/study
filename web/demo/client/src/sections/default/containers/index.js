/**
 * Created by PengLing on 2016/4/29.
 */
 'use strict';
 import React from 'react';
 import {connect} from 'react-redux';
 
 class Home extends React.Component {
     render() {
         return <div>{<div>首页OJBK</div> }</div>;
     }
 }
 
 export default connect()(Home);