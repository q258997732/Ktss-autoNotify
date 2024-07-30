package com.bob.autonotify.schedule;

import com.bob.autonotify.animations.Animations;
import com.bob.autonotify.entity.NotifyEventEntity;
import com.bob.autonotify.notification.Notification;
import com.bob.autonotify.notification.Notifications;
import com.bob.autonotify.notification.TrayNotification;
import com.bob.autonotify.util.KpineUtil;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NotifySchedule {

	private static final Logger LOGGER = LogManager.getLogger(NotifySchedule.class);
	final CountDownLatch latch = new CountDownLatch(1);
	private final int getNotifyListInitialDelay = 0;
	private final int getNotifyListDelay = 2000;
	private final int corePoolSize = 10;
	private final int maxPoolSize = corePoolSize * 5;
	// 积累队列
	final private List<NotifyEventEntity> trayNotifyList = new ArrayList<>();
	final private List<NotifyEventEntity> speechNotifyList = new ArrayList<>();
	// 临时队列
	final private List<NotifyEventEntity> tmpTrayNotifyList = new ArrayList<>();
	final private List<NotifyEventEntity> tmpSpeechNotifyList = new ArrayList<>();
	ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(corePoolSize);


	// 获取报警清单
	public void getNotifyList() {
		System.out.println("启动获取报警清单Schedule.");
		Runnable getNotifyTask = new Runnable() {
			@Override
			public void run() {
				try {
					List<NotifyEventEntity> tmpList = KpineUtil.getNotifyEventList();
					trayNotifyList.addAll(tmpList);
					speechNotifyList.addAll(tmpList);
					if(tmpTrayNotifyList.isEmpty()){
						tmpTrayNotifyList.addAll(trayNotifyList);
						trayNotifyList.clear();
					}
					if(tmpSpeechNotifyList.isEmpty()){
						tmpSpeechNotifyList.addAll(speechNotifyList);
						speechNotifyList.clear();
					}
					System.out.printf("同步事件任务 语音队列:%s 弹窗队列:%s%n",speechNotifyList.size(),trayNotifyList.size());
//					System.out.printf("临时语音队列:%s 临时弹窗队列:%s%n",tmpSpeechNotifyList.size(),tmpTrayNotifyList.size());
				} catch (Exception e) {
					LOGGER.error("获取报警清单失败{}", e.getMessage());
					e.printStackTrace();
				}
				scheduler.schedule(this, getNotifyListDelay, TimeUnit.MILLISECONDS);
			}
		};
		scheduler.schedule(getNotifyTask, getNotifyListInitialDelay, TimeUnit.MILLISECONDS);
	}

	public void speechTask(long initialDelay, long delay) {
		scheduler.setMaximumPoolSize(maxPoolSize/2);
		System.out.println("启动语音Schedule.");
		// 创建ActiveXComponent实例
		ActiveXComponent activeXComponent = new ActiveXComponent("Sapi.SpVoice");
		Dispatch spVoice = activeXComponent.getObject();
		Runnable speechEventTask = new Runnable() {
			@Override
			public void run() {
				Iterator<NotifyEventEntity> iterator = tmpSpeechNotifyList.iterator();
				if (!tmpSpeechNotifyList.isEmpty()) {
					while (iterator.hasNext()) {
						NotifyEventEntity notifyEventEntity = iterator.next();
						Dispatch.call(spVoice, "Speak", new Variant(notifyEventEntity.toString()));
						System.out.println("语音播报:"+notifyEventEntity.toString());
						iterator.remove();
					}
				}
				scheduler.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
			}
		};
		scheduler.schedule(speechEventTask, delay, TimeUnit.MILLISECONDS);
	}

	public void trayTask(long initialDelay, long delay) {
		scheduler.setMaximumPoolSize(maxPoolSize/2);
		System.out.println("启动弹窗Schedule.");
		tmpTrayNotifyList.addAll(trayNotifyList);
		Runnable trayEventTask = new Runnable() {
			@Override
			public void run() {
				// 弹窗任务
				Iterator<NotifyEventEntity> iterator = tmpTrayNotifyList.iterator();
				if (!tmpTrayNotifyList.isEmpty()) {
					String title = KpineUtil.getTitle();
					while (iterator.hasNext()) {
						NotifyEventEntity notifyEventEntity = iterator.next();
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
							tray.setMessage(notifyEventEntity.toString());
							tray.setNotification(notification);
							tray.setAnimation(Animations.POPUP);
//						tray.showAndDismiss(Duration.seconds(5));
							tray.showAndWait();
							System.out.println("报警弹窗:"+notifyEventEntity.toString());
						});
						// 弹窗间隔事件
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						iterator.remove();
					}
				}
				scheduler.schedule(this, initialDelay, TimeUnit.MILLISECONDS);
			}
		};
		scheduler.schedule(trayEventTask, delay, TimeUnit.MILLISECONDS);
	}

}
