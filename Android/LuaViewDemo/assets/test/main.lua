--
-- Created by IntelliJ IDEA.
-- User: song
-- Date: 16/3/30
-- Time: 上午10:32
-- To change this template use File | Settings | File Templates.
--


view1 = View()
view1.cornerRadius(25)
view1.frame(50, 50, 100, 20)
view1.backgroundColor(0x0000ff, 0.5)


container = View()
container.frame(51, 51, 10, 20)


view2 = View()
container.addView(view2)
view2.cornerRadius(25)
view2.frame(0, 0, 98, 18)
view2.backgroundColor(0xff0000, 0.8)




