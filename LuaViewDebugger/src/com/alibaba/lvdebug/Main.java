package com.alibaba.lvdebug;

public class Main {

	public static void main(String[] args) {
		// 关闭老的
		String ret = new ExitHttpRequest().sendExitCmd();
		if (ret != null && "ok".equals(ret.trim())) {
			System.out.println("关闭...");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("启动...");

		// 启动socket调试端口监听服务
		new ServerMain().main();

	}

}
