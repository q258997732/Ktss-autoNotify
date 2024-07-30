package com.bob.autonotify;

import com.bob.autonotify.schedule.NotifySchedule;

public class AutoNotifyAlarm {

	public static void main(String[] args) {
		NotifySchedule notifySchedule = new NotifySchedule();
		notifySchedule.getNotifyList();
		notifySchedule.speechTask(1000, 1000);
		notifySchedule.trayTask(1000, 1000);
	}
}

