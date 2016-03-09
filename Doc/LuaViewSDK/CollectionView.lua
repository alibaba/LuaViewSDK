--- CollectionView
-- 类似于Cell大小任意的TableView
--
-- CollectionView的父类是[ScrollView](./ScrollView.html)
-- @classmod CollectionView


--- 通过这个方法创建一个CollectionView对象
-- 设置Collection代理
-- @tparam table dataSource 数据源
-- @usage  -- 例子二:
-- local collectionView = CollectionView{
--         Section = {
--             SectionCount = 3, -- section 数量
-- 
--             RowCount = ^(section){ -- 每个section对应的行数
--                 if ( section == 0 ){
--                     -- 广告栏
--                     return 0;
--                 } else if( section==1 ) {
--                     -- 十点上新提示
--                     local theBrandMind = getOptionTheBrandMind(i);
--                     if( theBrandMind ) {
--                         return 1; 
--                     } else {
--                         return 0;
--                     }
--                 }
--                 -- 品牌团坑位
--                 return getOptionItemsNum(i, section);
--             },
--             --Spacing = ^(section ){ -- 行间距
--             --    return 0;
--             --},
--             --EdgeInsets = ^(section ){-- cell边距
--             --   return 0,0,0,0;
--             --}
--   
--         },
--         Cell = { 
--             Id = function ( section, row ){ -- cell唯一标识
--                 if( section == 0 ) {
--                     return "广告栏";
--                 } else if( section == 1 ) {
--                     return "十点上新";
--                 } else {
--                     return "品牌团";
--                 }
--             }
--             "品牌团" = { -- 品牌团cell样式
--                 Size = ^(section, row){ -- 每个Cell 尺寸
--                     return scrW , (178 * scrW / 375);
--                 },
--                 Init = ^(cell){
--                    BrandCellInit(cell);
--                    cell.loading = LoadingIndicator();
--                 },
--                 Layout = ^(cell , section, row){
--                     local model = getOptionModel( i, section, row);
--                     if ( model ) {
--                         BrandCellLayout(cell, model );
--                     }
--                     cell.loading.startAnimating();
--                     cell.loading.center(160,50);
--                 },
--                 Select = ^(section, row){
--                     print(section, row);
--                     System.gc();
--                 }
--             },
--             "十点上新" = { -- 十点上新 cell 样式
--                 Size = ^(section, row){ -- 每个Cell 尺寸
--                     return scrW , NOTICE_CELL_H;
--                 },
--                 Init = ^(cell){
--                    local cellW, cellH = window.size();
--   
--                    cell.line = Label();
--                    cell.line.frame(0,0,scrW-20,1);
--                    cell.line.center(cellW/2, cellH/2);
--                    cell.line.backgroundColor(0x333333);
--   
--                    cell.label = Label();
--                    cell.label.frame(0, 0, cellW, cellH);
--                    cell.label.font(14);
--                    cell.label.textAlignment(TextAlignmentCenter);
--                    window.backgroundColor(0xeeeeee);
--                    cell.label.backgroundColor(0xeeeeee);
--                 },
--                 Layout = ^(cell , section, row){
--                     local cellW, cellH = window.size();
--                     local theBrandMind = getOptionTheBrandMind(i);
--                     cell.label.text(theBrandMind );
--                     cell.label.sizeToFit();
--                     local w,h = cell.label.size();
--                     w = w + 20*2;
--                     cell.label.size(w,h);
--                     cell.label.center(cellW/2, cellH/2);
--                 },
--                 Callback = ^(section, row){
--                     print(section, row);
--                     System.gc();
--                 }
--             }
--         },
--         Callback = {
--            PullDown = ^(){ -- 开始下拉刷新回调
--                print("HeaderRefresh");
--                option.requestItems(^(){
--                        local collectionView = tableViewArray[i];
--                        collectionView.reloadData();
--                        collectionView.headerEndRefreshing();
--                    });
--            }
--		   }
--     };
--     collectionView.miniSpacing(5); -- 设置最小间距
--     local barH = 40;
--     collectionView.frame( (i-1)*scrW, barH, scrW, scrH-barH);
--     collectionView.backgroundColor(0xeeeeee);
--     collectionView.initRefreshHeader();
--   
function CollectionView()
end

--    
--- 重新加载刷新CollectionView
function reload()
end

--- 设置最小间距
-- @number miniSpacing 最小间距
function miniSpacing()
end

--- 获取最小间距
-- @treturn number miniSpacing 最小间距
function miniSpacing()
end






