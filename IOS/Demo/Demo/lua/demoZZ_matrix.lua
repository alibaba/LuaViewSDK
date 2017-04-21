-- 简单动画
view = Button();
view:frame(10, 10, 60, 120)

view:matrix( 1,2,3,4,5,6 );
view:matrix( {2,3,4,5,6,7} );

arr = view:matrix();
print(arr[1],arr[2],arr[3],arr[4],arr[5],arr[6] );
