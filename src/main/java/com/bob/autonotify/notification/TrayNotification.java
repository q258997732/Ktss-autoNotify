package com.bob.autonotify.notification;

import com.bob.autonotify.animations.Animation;
import com.bob.autonotify.animations.Animations;
import com.bob.autonotify.models.CustomStage;
import com.bob.autonotify.util.KpineUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;

public final class TrayNotification {

	@FXML
	private Label lblTitle, lblClose;
	@FXML
	private TextArea TtaMessage;
	@FXML
	private ImageView imageIcon;
	@FXML
	private Rectangle rectangleColor;
	@FXML
	private AnchorPane rootNode;
	@FXML
	private Button btnConfirm, btnIgnore;


	private CustomStage stage;
	private Notification notification;
	private Animation animation;
	private EventHandler<ActionEvent> onDismissedCallBack, onShownCallback;

	@Getter
	@Setter
	private String eventId;

	/**
	 * Initializes an instance of the tray notification object
	 *
	 * @param title         The title text to assign to the tray
	 * @param body          The body text to assign to the tray
	 * @param img           The image to show on the tray
	 * @param rectangleFill The fill for the rectangle
	 */
	public TrayNotification(String title, String body, Image img,
							Paint rectangleFill, Notification notification) {
		initTrayNotification(title, body, notification);

		setImage(img);
		setRectangleFill(rectangleFill);
	}

	/**
	 * Initializes an instance of the tray notification object
	 *
	 * @param title        The title text to assign to the tray
	 * @param body         The body text to assign to the tray
	 * @param notification The notification type to assign to the tray
	 */
	public TrayNotification(String title, String body, Notification notification) {
		initTrayNotification(title, body, notification);
	}

	/**
	 * Initializes an empty instance of the tray notification
	 */
	public TrayNotification(Notification notification) {
		this("", "", notification);
	}

	/**
	 * Initializes the tray notification with the default type.
	 */
	public TrayNotification() {
		this(Notifications.NOTICE);
	}

	private void initTrayNotification(String title, String message, Notification type) {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("views/TrayNotification.fxml"));
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		initStage();
		initAnimations();

		setTray(title, message, type);
	}

	private void initAnimations() {
		setAnimation(Animations.SLIDE); // Default animation type
	}

	private void initStage() {
		stage = new CustomStage(rootNode, StageStyle.UNDECORATED);
		stage.setScene(new Scene(rootNode));
		stage.setAlwaysOnTop(true);

		/* 修改窗口显示初始位置 */
//		stage.setLocation(stage.getBottomRight());

		/* 设置窗体在屏幕绝对居中 */
//		stage.centerOnScreen();

		// 获取窗体大小
		double initialWidth = rootNode.getPrefWidth();
		double initialHeight = rootNode.getPrefHeight();
//		System.out.println("initialWidth:"+initialWidth+" initialHeight:"+initialHeight);

		/* 计算屏幕中心点桌标 */
		Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
		double screenCenterX = visualBounds.getWidth() / 2;
		double screenCenterY = visualBounds.getHeight() / 2;
//		System.out.println("screenCenterX:"+screenCenterX+" screenCenterY:"+screenCenterY);

		// 计算舞台的起始坐标
		double stageX = screenCenterX - initialWidth / 2;
		double stageY = screenCenterY - initialHeight / 2;
//		System.out.println("stageX:"+stageX+" stageY:"+stageY);

		// 设置舞台的位置
		stage.setX(stageX);
		stage.setY(stageY);


		// 设置按钮动作
		btnConfirm.setOnMouseClicked(e -> confirm());
		btnIgnore.setOnMouseClicked(e -> ignore());
//		lblClose.setOnMouseClicked(e -> dismiss());
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification nType) {
		notification = nType;

		URL imageLocation = getClass().getClassLoader().getResource(nType.getURLResource());
		setRectangleFill(Paint.valueOf(nType.getPaintHex()));
		setImage(new Image(imageLocation.toString()));
		setTrayIcon(imageIcon.getImage());
	}

	public void setTray(String title, String message, Notification type) {
		setTitle(title);
		setMessage(message);
		setNotification(type);
	}

	public void setTray(String title, String message, Image img, Paint rectangleFill, Animation animation) {
		setTitle(title);
		setMessage(message);
		setImage(img);
		setRectangleFill(rectangleFill);
		setAnimation(animation);
	}

	public boolean isTrayShowing() {
		return animation.isShowing();
	}

	/**
	 * Shows and dismisses the tray notification
	 *
	 * @param dismissDelay How long to delay the start of the dismiss animation
	 */
	public void showAndDismiss(Duration dismissDelay) {
		if (!isTrayShowing()) {
			stage.show();

//			onShown();
//			animation.playSequential(dismissDelay);
		} else dismiss();

		onDismissed();
	}

	/**
	 * Displays the notification tray
	 */
	public void showAndWait() {
		if (!isTrayShowing()) {
			stage.show();

			animation.playShowAnimation();

			onShown();
		}
	}

	/**
	 * Dismisses the notifcation tray
	 */
	public void dismiss() {
		if (isTrayShowing()) {
			animation.playDismissAnimation();
			onDismissed();
		}
	}

	public void closeStage(){
		stage.close();
	}

	/**
	 * 点击确认已处理按钮动作
	 */
	public void confirm() {
		try {
			boolean result = KpineUtil.resolveMonitorEvent(eventId, "1");
			if (result) {
				System.out.printf("事件:%s 确认已处理%n", eventId);
			} else {
				System.out.printf("事件:%s 确认处理失败%n", eventId);
			}
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			closeStage();
		}
	}

	public void ignore() {
		try {
			boolean result = KpineUtil.resolveMonitorEvent(eventId, "2");
			if (result) {
				System.out.printf("事件:%s 已忽略%n", eventId);
			} else {
				System.out.printf("事件:%s 忽略失败%n", eventId);
			}
		} catch (IOException | SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			closeStage();
		}
	}

	private void onShown() {
		if (onShownCallback != null)
			onShownCallback.handle(new ActionEvent());
	}

	private void onDismissed() {
		if (onDismissedCallBack != null)
			onDismissedCallBack.handle(new ActionEvent());
	}

	/**
	 * Sets an action event for when the tray has been dismissed
	 *
	 * @param event The event to occur when the tray has been dismissed
	 */
	public void setOnDismiss(EventHandler<ActionEvent> event) {
		onDismissedCallBack = event;
	}

	/**
	 * Sets an action event for when the tray has been shown
	 *
	 * @param event The event to occur after the tray has been shown
	 */
	public void setOnShown(EventHandler<ActionEvent> event) {
		onShownCallback = event;
	}

	public Image getTrayIcon() {
		return stage.getIcons().get(0);
	}

	/**
	 * Sets a new task bar image for the tray
	 *
	 * @param img The image to assign
	 */
	public void setTrayIcon(Image img) {
		stage.getIcons().clear();
		stage.getIcons().add(img);
	}

	public String getTitle() {
		return lblTitle.getText();
	}

	/**
	 * Sets a title to the tray
	 *
	 * @param txt The text to assign to the tray icon
	 */
	public void setTitle(String txt) {
		Platform.runLater(() -> lblTitle.setText(txt));
	}

	public String getMessage() {
		return TtaMessage.getText();
	}

	/**
	 * Sets the message for the tray notification
	 *
	 * @param txt The text to assign to the body of the tray notification
	 */
	public void setMessage(String txt) {
		TtaMessage.setText(txt);
	}

	public Image getImage() {
		return imageIcon.getImage();
	}

	public void setImage(Image img) {
		imageIcon.setImage(img);

		setTrayIcon(img);
	}

	public Paint getRectangleFill() {
		return rectangleColor.getFill();
	}

	public void setRectangleFill(Paint value) {
		rectangleColor.setFill(value);
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void setAnimation(Animations animation) {
		setAnimation(animation.newInstance(stage));
	}

}
