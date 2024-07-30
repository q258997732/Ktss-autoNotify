package com.bob.autonotify;

import com.bob.autonotify.animations.Animations;
import com.bob.autonotify.notification.Notification;
import com.bob.autonotify.notification.Notifications;
import com.bob.autonotify.notification.TrayNotification;
import com.bob.autonotify.util.KpineUtil;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class TestTmp {
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		String title = KpineUtil.getTitle();
		/* 弹窗 */
		Notification notification = Notifications.NOTICE;
		SwingUtilities.invokeLater(() -> {
			new JFXPanel(); // initializes JavaFX environment
			latch.countDown();
		});

		try {
			latch.await(); //
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		Platform.runLater(() -> {
			TrayNotification tray = new TrayNotification();
			tray.setTitle(title);
			tray.setMessage("Test Message");
			tray.setNotification(notification);
			tray.setAnimation(Animations.POPUP);
//						tray.showAndDismiss(Duration.seconds(5));
			tray.showAndWait();
//			System.out.println("报警弹窗测试");
		});
		// 弹窗间隔事件
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);

		}
	}
}
