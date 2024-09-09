package com.bob.autonotify;

import com.bob.autonotify.animations.Animations;
import com.bob.autonotify.notification.Notification;
import com.bob.autonotify.notification.Notifications;
import com.bob.autonotify.notification.TrayNotification;
import com.bob.autonotify.util.KpineUtil;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class TestTmp {
	public static void main(String[] args) throws InterruptedException {

		/* 测试窗体效果 */
		final CountDownLatch latch = new CountDownLatch(1);
		String title = KpineUtil.getTitle();

		/* 弹窗 */
		Notification notification = Notifications.NOTICE;
		SwingUtilities.invokeLater(() -> {
			System.out.println("initializes JavaFX environment");
			new JFXPanel(); // initializes JavaFX environment

			latch.countDown();
		});

		latch.await();

		Platform.setImplicitExit(false);
		Platform.runLater(() -> {
			System.out.println("弹窗测试");
			TrayNotification tray = new TrayNotification();
			tray.setEventId("3424181496CA4C5D99AAE5C03FD9F9DD");
			tray.setTitle(title);
			tray.setMessage("【******】**新数据采集通知 基于在 中证指数 数据源中的采集任务 采" +
					"集到了 关于发布中证港股通中央企业综合指数等“条指数的公告 信息，请" +
					"自行登陆系统进行查看");
			tray.setNotification(notification);
			tray.setAnimation(Animations.FADE);
//				tray.showAndDismiss(Duration.seconds(5));
			tray.showAndWait();
			System.out.println("报警弹窗测试");

		});
		System.out.println("弹窗测试结束:" + latch.getCount());
		// 弹窗间隔事件
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
	}

}
