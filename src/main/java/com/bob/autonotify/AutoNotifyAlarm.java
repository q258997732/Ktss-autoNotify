package com.bob.autonotify;

import com.bob.autonotify.animations.Animations;
import com.bob.autonotify.entity.NotifyEventEntity;
import com.bob.autonotify.entity.NotifyResponseEntity;
import com.bob.autonotify.notification.Notification;
import com.bob.autonotify.notification.Notifications;
import com.bob.autonotify.notification.TrayNotification;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class AutoNotifyAlarm {

	private static TrayNotification tray;

	public static void main(String[] args) throws InterruptedException, IOException, ParserConfigurationException, SAXException {
		final CountDownLatch latch = new CountDownLatch(1);


		/* 读取xml配置文件 */
		// 创建DocumentBuilderFactory实例
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// 创建DocumentBuilder实例
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		// 解析当前目录下的XML文件
		Document doc = dBuilder.parse(new File("src/main/resources/AutoNotifyConfig.xml"));
		// 正常化文档，以便更容易地读取
		doc.getDocumentElement().normalize();

		// 读取配置文件参数
		String apiHost = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("HOST").item(0).getTextContent();
		String apiPort = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("PORT").item(0).getTextContent();
		String apiUser = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("USER").item(0).getTextContent();
		String apiToken = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("TOKEN").item(0).getTextContent();
		String title = ((Element) doc.getElementsByTagName("NOTIFY").item(0)).getElementsByTagName("TITLE").item(0).getTextContent();
        String aStatus = doc.getElementsByTagName("ASTATUS").item(0).getTextContent();

        while( !"off".equals(aStatus)) {
			try {
				aStatus = doc.getElementsByTagName("ASTATUS").item(0).getTextContent();
				RestTemplate restTemplate = new RestTemplate();
				String url = String.format("http://%s:%s", apiHost, apiPort);
				String uri = "/api/open/k-tss/queryEvent";
				Map<String, String> params = new HashMap<>();
				params.put("token", apiToken);
				params.put("cur_user", apiUser);
				url = String.format("%s%s?token=%s&cur_user=%s", url, uri, params.get("token"), params.get("cur_user"));
//		System.out.println(url);
				System.out.printf("正在获取 %s 报警事件%n",apiUser);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				//HttpEntity<String> entity = new HttpEntity<>(/* JSON字符串 */, headers);

				// 发送POST请求并接收响应
				ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
//				System.out.println("URL: " + url);
				String responseBody = responseEntity.getBody();
//				System.out.println("responseBody: " + responseBody);
				ObjectMapper objectMapper = new ObjectMapper();
				NotifyResponseEntity notifyResponseEntity = objectMapper.readValue(responseBody, NotifyResponseEntity.class);
				List<NotifyEventEntity> entities = notifyResponseEntity.getData();

				System.out.println(new Date() + " 通知事件数:" + entities.size());

				// 循环弹窗
				for (NotifyEventEntity event : entities) {
					String message = String.format("%s %s %s", event.getUsername(), event.getxCarNumber(), event.getxAlarm());
					/* 弹窗 */
					Notification notification = Notifications.NOTICE;
					SwingUtilities.invokeLater(() -> {
						new JFXPanel(); // initializes JavaFX environment
						latch.countDown();
					});
					latch.await();

					Platform.runLater(() -> {
						TrayNotification tray = new TrayNotification();
						tray.setTitle(title);
						tray.setMessage(message);
						tray.setNotification(notification);
						tray.setAnimation(Animations.POPUP);
//						tray.showAndDismiss(Duration.seconds(5));
						tray.showAndWait();
//			tray.showAndWait();
					});
					// 弹窗间隔事件
					Thread.sleep(1000);
				}

				Thread.sleep(2000);
			}catch(Exception e){
				e.printStackTrace();
			}
        }
		//--------------------------以下为chrom driver -----------------------------
		// 设置ChromeDriver的路径
//        System.setProperty("webdriver.chrome.driver", "D:\\Tools\\chromedriver-win64\\chromedriver.exe");
//
//        // 创建WebDriver实例
//
//        ChromeOptions options = new ChromeOptions();
//        // 允许远程连接
//        //options.addArguments("--remote-allow-origins=*");
//        // 启用无头模式
//        options.addArguments("--headless");
//        // 禁用GPU加速
//        options.addArguments("--disable-gpu");
//        WebDriver driver = new ChromeDriver(options);

//        try {
//            // 打开网页
//            driver.get("http://111.230.199.118:9776/Speechify/");
//
//            // 获取JavaScript执行器
//            JavascriptExecutor js = (JavascriptExecutor) driver;
//
//            // 执行JavaScript代码
//            // 例如，获取页面标题
//            String stitle = (String) js.executeScript("return document.title;");
//            System.out.println("Page stitle: " + stitle);
//
//            // 你可以执行更复杂的JavaScript代码
//            js.executeScript("document.getElementById('text-input').value = 'java调用语音';");
//            js.executeScript("document.getElementById('synthesize').click();");
//            Thread.sleep(30000);
////             js.executeScript("...your JavaScript code here...");
//
//        } finally {
//            // 关闭浏览器
//            driver.quit();
	}

//		webClient.post()
//				.uri("/convert", "你好老阴逼") // 假设你的服务有一个/convert端点接受文本
//				.accept(MediaType.APPLICATION_OCTET_STREAM)
//				.retrieve()
//				.bodyToFlux(byte[].class)
//				.map(Flux::just)
//				.collectList()
//				.map(bytes -> ResponseEntity.ok().contentType(MediaType.parseMediaType("audio/mpeg")).body(bytes))
//				.defaultIfEmpty(ResponseEntity.notFound().build())
//				.subscribe();
//    }
}

