/**
 * Created by liu.xinyi
 * on 2016/3/31.
 */
'use strict';
import React from 'react';
import {footer} from './style.css';

export default class Footer extends React.Component {
    render() {
        const {footerProps} = this.props;

        return (
            <div className={footer} {...footerProps}>
                {this.props.children}
            </div>
        );
    }
};