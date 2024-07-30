package com.bob.autonotify.util;

import com.bob.autonotify.entity.NotifyEventEntity;
import com.bob.autonotify.entity.NotifyResponseEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KpineUtil {

	public static List<NotifyEventEntity> getNotifyEventList() throws ParserConfigurationException, IOException, SAXException {
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

		// 发送请求
		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("http://%s:%s", apiHost, apiPort);
		String uri = "/api/open/k-tss/queryEvent";
		Map<String, String> params = new HashMap<>();
		params.put("token", apiToken);
		params.put("cur_user", apiUser);
		url = String.format("%s%s?token=%s&cur_user=%s", url, uri, params.get("token"), params.get("cur_user"));
		System.out.println(url);
//		System.out.printf("正在获取 %s 报警事件%n", apiUser);
		// 设置头部
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// 发送POST请求并接收响应
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
		String responseBody = responseEntity.getBody();
//		System.out.println("responseBody:" + responseBody);
		ObjectMapper objectMapper = new ObjectMapper();
		NotifyResponseEntity notifyResponseEntity = objectMapper.readValue(responseBody, NotifyResponseEntity.class);
		List<NotifyEventEntity> entities = notifyResponseEntity.getData();

//		System.out.println(new Date() + " 通知事件数:" + entities.size());

		return entities;
	}

	public static String getTitle() {
		try {
			/* 读取xml配置文件 */
			// 创建DocumentBuilderFactory实例
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			// 创建DocumentBuilder实例
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			// 解析当前目录下的XML文件
			Document doc = dBuilder.parse(new File("src/main/resources/AutoNotifyConfig.xml"));
			// 正常化文档，以便更容易地读取
			doc.getDocumentElement().normalize();
			return ((Element) doc.getElementsByTagName("NOTIFY").item(0)).getElementsByTagName("TITLE").item(0).getTextContent();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return "未知账号";
	}
}
