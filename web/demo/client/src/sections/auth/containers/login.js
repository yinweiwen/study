/**
 * Created by liu.xinyi
 * on 2016/3/31.
 */
'use strict';
import React from 'react';
import {connect} from 'react-redux';
import {push} from 'react-router-redux';
import {Button, Input, Form, Row, Col, message} from 'antd';
const FormItem = Form.Item;
import {login} from '../actions/auth';

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: ''
        }
    }

    componentWillReceiveProps(nextProps) {
        const {user, error, dispatch} = nextProps;
        if (error) {
            message.error(error);
            this.setState({password: ''});
        }

        if (user && user.authorized) {
            dispatch(push('/'));
        }
    }

    componentDidMount() {
        const {user, dispatch} = this.props;
        if (user && user.authorized) {
            dispatch(push('/'));
        }
    }

    loginHandler = _=> {
        this.props.dispatch(login(this.state.username, this.state.password));
    };

    enterHandler = e=> {
        if(e.key === 'Enter'){
            this.props.dispatch(login(this.state.username, this.state.password));
        }
    };

    render() {
        const {isRequesting, user, dispatch} = this.props;
        if (user && user.authorized) {
            dispatch(push('/'));
            return <div/>
        }

        return (
            <Row style={{ paddingTop: 200 }}>
                <Col span="6" offset="9">
                    <Form onKeyDown={this.enterHandler}>
                        <FormItem>
                            <Input type="text" value={this.state.username} placeholder="用户名"
                                   onChange={e=> this.setState({username: e.target.value})}/>
                        </FormItem>
                        <FormItem>
                            <Input type="password" value={this.state.password} placeholder="密码"
                                   onChange={e=> this.setState({password: e.target.value})}/>
                        </FormItem>
                        <FormItem>
                            <Button type="primary" style={{width: '100%'}} loading={isRequesting} onClick={this.loginHandler}>登录</Button>
                        </FormItem>
                    </Form>
                </Col>
            </Row>
        );
    }
}

function mapStateToProps(state) {
    const {auth} = state;
    return {
        user: auth.user,
        error: auth.error,
        isRequesting: auth.isRequesting
    }
}

export default connect(mapStateToProps)(Login);