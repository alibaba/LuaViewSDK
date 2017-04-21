-- Created by LuaView.
-- Copyright (c) 2017, Alibaba Group. All rights reserved.
--
-- This source code is licensed under the MIT.
-- For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.

view = Image();
view.backgroundColor(0xff0000, 0.3)
view.image("animate1")
view.frame(0, 64, 200, 200)

view.callback(function()
    --view.matrix( {2,3,4,5,6,7} );
    view.matrix(1, 2, 1, 1, 0, 1);

    arr = view.matrix();
    print(arr[1],arr[2],arr[3],arr[4],arr[5],arr[6] );

    view.invalidate()

    Toast("done")

end)

