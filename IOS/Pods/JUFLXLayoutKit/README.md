# JUFLXLayoutKit

## 背景

众所周知, iOS布局方式在autolayout之前通常是`addSubview:`, `sizeThatFits:`, `layoutSubviews`这三个步骤。

吐槽点如下:

1. 代码风格差一点的布局, `addSubview:`在同一个.m文件里到处都是, View的父子关系往往不是一目了然的。
2. `layoutSubviews`里的人工计算, 比如让两个文字label在容器里一起居中, 通常做法是先计算他们的长度, 再各自`setFrame`。

传统代码可能如下:
	
	- (void)setupSubviews {
	    labelA = [UILabel new];
	    labelB = [UILabel new];
	    
	    [self addSubview:labelA];
	    [self addSubview:labelA];
	}

	- (void)layoutSubviews {
		[super layoutSubviews];
		
	    CGRect containerRect = ...;
	    [labelA sizeToFit];
	    [labelB sizeToFit];
	    CGFloat totalLabelWidth = CGRectGetWidth(labelA.frame) + CGRectGetWidth(labelB.frame) + between;
	    CGFloat between = (CGRectGetWidth(containerRect) - totalLabelWidth) / 3.0;
	    CGFloat labelAStartX = between;
	    CGFloat labelAStartY = (CGRectGetHeight(containerRect) - CGRectGetHeight(labelA.frame)) * 0.5;
	    labelA.frame = CGRectMake(labelAStartX, labelAStartY, CGRectGetWidth(labelA.frame), CGRectGetHeight(labelA.frame));
	    
	    CGFloat labelBStartX = CGRectGetMaxX(labelA.frame) + between;
	    CGFloat labelBStartY = (CGRectGetHeight(containerRect) - CGRectGetHeight(labelB.frame)) * 0.5;
	    labelB.frame = CGRectMake(labelBStartX, labelBStartY, CGRectGetWidth(labelB.frame), CGRectGetHeight(labelB.frame));
	}
		
从结果看, 我们都要自己计算而且代码有点长冗长。
	
针对这两个痛点, 我们希望布局代码能够一目了然点, 少做点冗长的计算。

`JUFLXLayoutKit` 尝试提供这种解决方案, 上面的这段代码在这里可以这样来完成

    nodeA = [JUFLXNode nodeWithView:labelA];
    nodeA.sizeToFit = YES;
    nodeB = [JUFLXNode nodeWithView:labelB];
    nodeB.sizeToFit = YES;

    JUFLXNode *node = [JUFLXNode nodeWithView:containerView children:@[nodeA, nodeB]]];
    [node bindingInlineCSS:@"justify-content: space-around, align-items: center"];
    [node layoutAysnc:YES completionBlock:NULL];

这里的优势如下:

1. 不必重写container的layoutSubViews
2. view的父子关系看上去较明显, containerView的subview有labelA和labelB
3. 不必进行布局计算和setFrame, 通过一些语义化的描述就能达到效果
4. bonus, label的字长度的计算是在背景线程里做的, 包括整体布局的计算

`JUFLXLayoutKit` 是基于Facebook开源项目[css-layout](https://github.com/facebook/css-layout)的UIKit封装。

当你需要做布局时, 可以把你的`UIView`(任何`UIView`子类)用JUFLXNode包装一下, 再对每个node用CSS3的[Flexbox](http://www.w3.org/TR/css3-flexbox/)来描述一下, 再layout一下. That's all!

`JUFLXNode`只参与UIKit的布局, 其他touch handling, response chain, animations, 都不会有影响, 一切照UIKit的方式来。

#####如果要兼容原来的代码或者还是喜欢原来的layoutSubview写法, JUFLXNode也可以用在你的子View class中.

比如在你的子view class `CustomView`用node进行布局:
	
	- (id)init {
		...
		
		self.ju_flxnode.childNodes = @[_subview1.ju_flxNode, _subview2.ju_flxNode];
		
		...
	}
	
	- (void)layoutSubviews {
	    [super layoutSubviews];
	    []self.ju_flxnode layoutAsync:YES];
	}

在你的ViewController里照常addSubview:

	- (void)viewDidLoad {
		...
	
		self.customView = [[CustomView alloc] initWithFrame:CGRectInset(self.view.bounds, 20)];
		[self.view addSubview:self.customView];
	}
	

`JUFLXNode`通过`UIView`的category对每个view懒初始化`ju_flx`

## Demo

运行Examples里的workspace, 就能看到demo。

+ `Simple` 表达一些默认设置, 以及flex属性的作用
+ `Middle` 设置一个比较长的scrollview, 并且scrollView的长度不用事先计算好, 而是根据子node的估计高度计算后设置的。
+ `Pro` 仿造聚客详情, 值得注意的是, bottom bar里中间的字在不同宽度下地表现, 如果宽度小, 会自动折行.
+ `Mix` 表达`JUFLXNode`布局不用从view tree的root开始进行, 也可以在view tree的分支开始, 只不过是对这个分支以及分支的子分支进行布局, 并不影响siblings以及father tree的布局。
+ `Table` 用`JUFLXNode` 来实现不同高度的tableviewCell布局计算。

下图是`Pro`里的截图:

这里使用Flex特性做到了横竖屏自适应。

![E7EB6B5D_0C86_4ECA_97CB_F2C657253851_jpeg](http://img1.tbcdn.cn/L1/461/1/6760e49ec9c71658010b5fe34e3b2d397f9f86db)
![CF556AAF_E0AF_4EE0_A485_D92B3CD37208_jpeg](http://img1.tbcdn.cn/L1/461/1/ace0eb4a05a021065efe907a8be7366c9dda3898)

## 系统要求

`iOS >= 6.0`

## 安装

```
pod 'JUFLXLayoutKit', :git => 'http://gitlab.alibaba-inc.com/juhuasuanwireless/JUFLXLayoutKit.git'
```

## 接入成本

#### 零成本！

JUFLXNode只负责当前view的size和它子view的frame。

> 可以从任何一个子view切入, 不会影响父view的布局以及子子view的布局。
> 包大小500k左右。

## 生命周期

1. `view retains node`, `node` is a `weak` property of `view`。
2. `view` 有且只有一个`node` instance property。
3. `node.childNodes = ...` 可以设置, 甚至可以直接`view.ju_flxNode.childNodes = ...`。如果`childNodes`的`view`没有被其他对象`retain`的话, 那么使用这个方法的时候, 这些view会被`removeFromSuperView`从而被释放掉。

总结, `view`管理`node`, 每个`view`有且只有一个`node`。如果`subViews`没有被其他对象`retain`的话，重新设置`node.childNodes`会导致`node.view`之前的`subViews`被释放掉。

## 技巧 & 注意点

+ Box----理解每个view都是一个box, box之间的间距是由margin来控制的。
+ Flexbox----理解Flexbox的各种属性。
+ 自上而下考虑Node, 从View tree的根节点。
+ 确保一个view tree里只有一个node执行layout。
+ `node`不负责`retain`它的`view`, 这个`view`最好被其他对象`retain`住, 比如:是实例变量, 被`addSubView`到某个被`retain`住的`view`上。

## Test

测试文件在`JUFLXLayoutKitFramework`里。

## 调试

针对`JUFLXLayoutKit`写的调试工具, 只会在DEBUG模式下链接, release包不会链接进去。

```
pod 'JUFLXLayoutDebugger', :git => 'git@gitlab.alibaba-inc.com:juhuasuanwireless/JUFLXLayoutDebugger.git', :configurations => ['Debug']
```
下图是调试工具的截图

![screenshot](http://img3.tbcdn.cn/L1/461/1/9edd200e3ea98d4c1839a527c856c42738e8ca95)

## 实践出真知

下面两个作业的环境在Demo工程文件里环境都已经搭好, 事件点击可以暂时不做处理。

#### 作业1

完成下图:

![screenshot](http://img2.tbcdn.cn/L1/461/1/7333d8a9ba640e03ef83cecf6f632837828b2f81)

环境介绍:

1. 打开`GettingStarted1ViewController`
2. 使用_itemModel的title, subTitle, ImageURL属性来自定义boxView


>需求: 

>1. 每个box, 由title, subtitle, image 三个组件组成。
>2. 这整个box里的每个小box可以随整体box大小进行自适应。


#### 作业2

2. 完成下图:

![screenshot](http://img4.tbcdn.cn/L1/461/1/6bf7a0a34794ecdfcd3f9a625da89e177729c73c)

环境介绍:

1. 打开`GettingStarted2ViewController`
2. 使用`_itemModel.promises`来做评论的tag, `_itemModel.comments`来做评论list的`datasource`

>需求:

>1. 整块view需要有segment, 评论标签, tableView三个组件组成。
>2. 评论标签这块可以根据评论的数量和评论字数长度自适应。
>3. tableView的高度随着评论字数和可能的图片自适应。(图片如果有, 那么在一行显示)


## License

JUFLXLayoutKit is available under the MIT license. See the LICENSE file for more info.
