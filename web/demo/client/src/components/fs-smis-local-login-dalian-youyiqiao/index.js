/**
 * Created by liu.xinyi
 * on 2016/4/8.
 */
'use strict';
import React from 'react';
import {push} from 'react-router-redux';
import {Button, Input, Form, Row, Col, message} from 'antd';
const FormItem = Form.Item;

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: ''
        }
    }

    static propTypes = {
        user: React.PropTypes.object.isRequired,
        error: React.PropTypes.string.isRequired,
        isRequesting: React.PropTypes.bool.isRequired,
        login: React.PropTypes.func.isRequired
    };

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
        this.props.dispatch(this.props.login(this.state.username, this.state.password));
    };

    enterHandler = e=> {
        if (e.key === 'Enter') {
            this.props.dispatch(this.props.login(this.state.username, this.state.password));
        }
    };

    render() {
        const {isRequesting, user, dispatch} = this.props;
        if (user && user.authorized) {
            dispatch(push('/'));
            return <div/>
        }

        return (
            <div>
                <h1 style={{textAlign:'center'}}>大连市北大友谊桥在线监测系统</h1>
                <Row style={{marginTop: 200, minHeight:this.state.minHeight}}>
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
                            <Row>
                                <Col span="3" offset="20">
                                    <Button type="primary" size="large" loading={isRequesting}
                                            onClick={this.loginHandler}>登录</Button>
                                </Col>
                            </Row>
                        </Form>
                    </Col>
                </Row>
            </div>
        );
    }
}

export default Login;