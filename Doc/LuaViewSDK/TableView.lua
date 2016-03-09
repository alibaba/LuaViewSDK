--- TableView
-- 每行高度样式可定制和重复利用的列表
--
-- TableView的父类是[ScrollView](./ScrollView.html)
-- @classmod TableView


--- 设置TableView代理
-- @tparam table source 数据源
-- @usage 
-- tableView = TableView{
--     Section = {
--         SectionCount = 2, -- section数量
--     
--         RowCount = ^(section){ -- 每个section的行数
--             return 10;
--         }
--     },
--   
--     Cell = {
--         Id = ^( section, row ){ -- 指定section和row对应的Cell类型唯一识别号
--             return  "图片+文字";
--         }
--         "图片+文字" = { -- cell 具体样式
--             Height = ^( section, row ){ -- 指定section和row 对应的高度
--                 return cellHeight;
--             }
--             Init = ^(cell, section, row){ -- 初始化cell
--                 cell.icon = Image();
--                 cell.title = Label();
--                 print("构造Cell");
--             },
--             Layout = ^(cell, section, row){ -- cell复用时调用
--                 cell.icon.frame(0, 0, cellHeight, cellHeight);
--                 cell.icon.image(imageUrl1);
--   
--                 cell.title.frame(cellHeight, 0, w-cellHeight, cellHeight);
--                cell.title.yext("测试"..section .."--" .. row);
--             },
--             Callack = ^(cell, section, row ){ -- 用户点击了section和row 
--                 print(section, row);
--                 tableView.reload();
--                 System.gc();
--             }
--     },
--     Callback = {
--         PullDown=^(){
--             -- 开始下拉刷新回调
--         },
--         PullUp = ^(){
--             -- 开始上拉刷新回调
--         },
--         Scrolling=^(){
--             -- 滚动中
--         },
--         ScrollBegin = ^(){
--             -- 开始滚动
--         },
--         ScrollEnd=^(){
--             -- 结束滚动
--         }     
--     }       
--  };       
--          
-- tableView.frame(0,50,w,h-100);
-- tableView.backgroundColor(0xffFFFF);
--   
-- local head = Button();
-- head.title("我是头");
-- head.frame(0,0,w,100);
-- head.backgroundColor(0xff);
-- tableView.header( head );
--   
-- local foot = Button();
-- foot.title("我是结尾");
-- foot.frame(0,0,w,100);
-- foot.backgroundColor(0xff0000);
-- tableView.footer( foot );
--    
function TableView()
end

---重新刷新加载TableView
function reload()
end

---设置TableView的头
-- @View header 头view
function header()
end

---设置TableView的尾
-- @View footer 尾view
function footer()
end

--- 是否正在下拉刷新动画中
function isRefreshing()
end

--- 代码控制 开始下拉刷新动画
function startRefreshing()
end

--- 停止下拉刷新动画(数据已经加载完成后调用)
function stopRefreshing()
end





--    
-- --         HeaderHeight = ^(section){ -- section的头高
-- --             return 30;
-- --         },
-- --         Header = ^(section){ -- section对应的头
-- --             local view = Button();
-- --             view.frame(0,0,w,30);
--                view.backgroundColor(0xffff,1);
-- --             view.title("section 头:"..section);
-- --             return view;
-- --         },
-- --   
-- --         FooterHeight = ^(section){ -- section的脚高
-- --             return 30;
-- --         },
-- --         Footer = ^(section){ -- section脚View
-- --             local view = Button();
-- --             view.frame(0,0,w,30);
-- --             view.backgroundColor(0xffff,1);
-- --             view.title("section 尾:"..section);
-- --             return view;
-- --         }