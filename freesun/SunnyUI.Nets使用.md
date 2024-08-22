## SunnyUI.Nets使用

在UCDS的开发选型中，最终根据我司技术栈及开发人员技能选择了以C#为主导的功能界面开发。在调研了几款UI库之后，选择了目前Github上star较多比较成熟的Sunny UI。SunnyUI.Net, 是一款基于.Net 4.0+、.Net 6 框架的 C# WinForm 开源控件库、工具类库、扩展类库、多页面开发框架。它的整体功能框架如下图所示：

![思维导图](https://camo.githubusercontent.com/5790586baa735988465fb96212ec5004d642f687bbf84234f3da0a47089844f2/68747470733a2f2f696d616765732e67697465652e636f6d2f75706c6f6164732f696d616765732f323032302f303632372f3231303031365f66333230336138625f3431363732302e706e67)

使用本款开源UI库，主要起到了美化界面、统一控件以及实现类似单页面的开发能力，能够很好的满足目前对统一采集分发系统，易用性、美观性的需求。同时，这个库还满足了国际化、多风格主体的定制的需求。

![输入图片说明](https://foruda.gitee.com/images/1695452615395997083/00202d42_416720.png)



另外，这个库还满足了图表展示的内容要求，它支持多种类型的图表展示。满足了在UCDS中对监测数据的查看和分析需求。



使用该库进行开发，需要新建Winform程序，并将Form类型手动修改为UIForm。开发的主界面如下：

![image-20240102104141741](C:\Users\yww08\AppData\Roaming\Typora\typora-user-images\image-20240102104141741.png)

着这个类似Navigate的主类型中，我们再添加各个功能页面，设置为Page：



```C#

        public FormNavigate()
        {
            InitializeComponent();
            //this.uiLabel1.Text = Global.AppName;
            int pageIndex = 1000;

            //uiNavBar1设置节点，也可以在Nodes属性里配置
            //uiNavBar1.Nodes.Add("管理");
            //uiNavBar1.Nodes.Add("统计");
            //uiNavBar1.Nodes.Add("设置");
            //uiNavBar1.SetNodePageIndex(uiNavBar1.Nodes[0], pageIndex);
            //uiNavBar1.SetNodeSymbol(uiNavBar1.Nodes[0], 561534);
            TreeNode parent = uiNavMenu1.CreateNode("管理", 561534, 24, pageIndex);
            uiNavMenu1.CreateChildNode(parent, AddPage(new PageConfigSensor(), ++pageIndex));
            //uiNavMenu1.CreateChildNode(parent, AddPage(new PageTestInstance(), ++pageIndex));
            //uiNavMenu1.CreateChildNode(parent, AddPage(new PagePaticipant(), ++pageIndex));

            pageIndex = 2000;
            //uiNavBar1.SetNodePageIndex(uiNavBar1.Nodes[1], pageIndex);
            //uiNavBar1.SetNodeSymbol(uiNavBar1.Nodes[1], 358721);
            parent = uiNavMenu1.CreateNode("统计", 358721, 24, pageIndex);
            uiNavMenu1.CreateChildNode(parent, AddPage(new PageConfigSensor(), ++pageIndex));

            pageIndex = 3000;
            //uiNavBar1.SetNodePageIndex(uiNavBar1.Nodes[2], pageIndex);
            //uiNavBar1.SetNodeSymbol(uiNavBar1.Nodes[2], 561534);
            parent = uiNavMenu1.CreateNode("设置", 561534, 24, pageIndex);
            uiNavMenu1.CreateChildNode(parent, AddPage(new PageConfigSensor(), ++pageIndex));

            //选中第一个节点
            uiNavMenu1.SelectPage(1001);

            //通过设置PageIndex关联，节点文字、图标由相应的Page的Text、Symbol提供
            //uiNavMenu1.CreateChildNode(parent, AddPage(new PageTest(), ++pageIndex));
        }
```

每个页面单独设置独立的Code，并且根据uiNavMenu1.CreateNode在主页面中添加跳转的功能。

同时UI库还提供了多种内置的弹框和通知能力，也可以减少开发的时间。

