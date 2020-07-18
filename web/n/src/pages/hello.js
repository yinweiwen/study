import {Card, Tabs, Tree} from 'antd';
import React, { Component } from 'react';

const style = {
    width: '400px',
    margin: '30px',
    boxShadow: '0 4px 8px 0 rgba(0, 0, 0, 0.2)',
    border: '1px solid #e8e8e8',
  };

  const TreeNode=Tree.TreeNode;

class Hello extends React.Component{
    state={
        activeKey:'1',
        expandedKeys:[],
    };
      
    onTabChange=(activeKey)=>{
        this.setState({activeKey});
    }

    onExpand=(eks)=>{
        this.setState({expandedKeys:eks});
    }

    onSelect=(sks)=>{
        const {expandedKeys}=this.state;
        const key=sks[0];

        if(expandedKeys.includes(key)){
            this.setState({
                expandedKeys:expandedKeys.filter(k=> k!==key)
            });
        }else{
            this.setState({expandedKeys:[...expandedKeys,key]});
        }
    }

    render(){
        return (
            <Tabs activeKey={this.state.activeKey} onChange={this.onTabChange}>
                <Tabs.TabPane tab="TAB 1" key="1">
                <Card style={style} actions={[<a>action 1</a>,<a>action 2</a>,]}>
                    <Card.Meta 
                    avatar={<img alt="" style={{width:'64px',height:'64px',borderRadius:'32px'}}
                    src="https://gw.alipayobjects.com/zos/rmsportal/WdGqmHpayyMjiEhcKoVE.png"/>}
                    title="AliPay"
                    description="只要你能听到我看到我的全心全意"
                    />
                    </Card>
                </Tabs.TabPane>
                <Tabs.TabPane tab="TAB 2" key="2">
                    <Tree
                     expandedKeys={this.state.expandedKeys}
                     selectedKeys={[]}
                     onExpand={this.onExpand}
                     onSelect={this.onSelect}>
                        <TreeNode title="parent1" key="0-0">
                            <TreeNode title="leaf1" key="0-0-1">
                                <TreeNode title="subleaf" key="0-0-1-1"/>
                            </TreeNode>
                            <TreeNode title="leaf2" key="0-0-2"></TreeNode>
                        </TreeNode>
                    </Tree>
                </Tabs.TabPane>
            </Tabs>
        );
    }
}

export default Hello;