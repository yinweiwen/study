/**
 * User: liuxinyi/liu.xinyi@free-sun.com.cn
 * Date: 2016/1/13
 * Time: 11:55
 *
 */
'use strict';

import React from 'react';

import Layout from './layout';
import Auth from './sections/auth';
import Default from './sections/default';

class App extends React.Component {
    constructor(props) {
        super(props);
    }    

    render() {
        document.title = this.props.projectName;
        let sections = [    
            Auth
        ];

        return (
            <Layout title={this.props.projectName} sections={sections}/>
        );
    }
}

export default App;