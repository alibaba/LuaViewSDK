-- 简单动画
view = Image();
view:image("button0.png");
view:frame(0, 64, 200, 200)

view:matrix( {2,3,4,5,6,7} );
view:matrix( 1,1,0,1,0,0 );

arr = view:matrix();
print(arr[1],arr[2],arr[3],arr[4],arr[5],arr[6] );
