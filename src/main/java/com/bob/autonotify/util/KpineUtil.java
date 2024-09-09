package com.bob.autonotify.util;

import com.bob.autonotify.entity.NotifyEventEntity;
import com.bob.autonotify.entity.NotifyResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

	private final static String AutoNotifyConfig = "src/main/resources/AutoNotifyConfig.xml";

	public static Map<String, Object> getConfig() throws ParserConfigurationException, IOException, SAXException {
		Map<String, Object> apiConnectConfig = new HashMap<>();
		/* 读取xml配置文件 */
		// 创建DocumentBuilderFactory实例
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// 创建DocumentBuilder实例
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		// 解析当前目录下的XML文件
		Document doc = dBuilder.parse(new File(AutoNotifyConfig));
		// 正常化文档，以便更容易地读取
		doc.getDocumentElement().normalize();

		// 读取配置文件参数
		String apiHost = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("HOST").item(0).getTextContent();
		String apiPort = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("PORT").item(0).getTextContent();
		String apiUser = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("USER").item(0).getTextContent();
		String apiToken = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("TOKEN").item(0).getTextContent();
		String title = ((Element) doc.getElementsByTagName("NOTIFY").item(0)).getElementsByTagName("TITLE").item(0).getTextContent();
		String aStatus = doc.getElementsByTagName("ASTATUS").item(0).getTextContent();

		apiConnectConfig.put("apiHost", apiHost);
		apiConnectConfig.put("apiPort", apiPort);
		apiConnectConfig.put("apiUser", apiUser);
		apiConnectConfig.put("apiToken", apiToken);
		apiConnectConfig.put("title", title);
		apiConnectConfig.put("aStatus", aStatus);

		return apiConnectConfig;
	}

	public static List<NotifyEventEntity> getNotifyEventList() throws ParserConfigurationException, IOException, SAXException {

		Map<String, Object> apiConnectConfig = getConfig();

		// 发送请求
		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("http://%s:%s", apiConnectConfig.get("apiHost").toString(), apiConnectConfig.get("apiPort").toString());
//		String uri = "/api/open/k-tss/queryEvent";
		String uri = "/api/open/k-tss/queryEvent";
		Map<String, String> params = new HashMap<>();
		params.put("token", apiConnectConfig.get("apiToken").toString());
		params.put("cur_user", apiConnectConfig.get("apiUser").toString());
		url = String.format("%s%s?token=%s&cur_user=%s", url, uri, params.get("token"), params.get("cur_user"));
//		System.out.println(url);
//		System.out.printf("正在获取 %s 报警事件%n", apiUser);
		// 设置头部
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);

		// 发送POST请求并接收响应
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
		String responseBody = responseEntity.getBody();
//		System.out.println("responseBody:" + responseBody);
		ObjectMapper objectMapper = new ObjectMapper();
		NotifyResponseEntity notifyResponseEntity = objectMapper.readValue(responseBody, NotifyResponseEntity.class);

//		System.out.println(new Date() + " 通知事件数:" + entities.size());

		return notifyResponseEntity.getData();
	}

	public static int insertMonitorEvent(String eventId, String realName) throws IOException, ParserConfigurationException, SAXException {
		Map<String, Object> apiConnectConfig = getConfig();

		// 发送请求
		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("http://%s:%s", apiConnectConfig.get("apiHost").toString(), apiConnectConfig.get("apiPort").toString());
		String uri = "/api/open/k-tss/insert_monitor_event_status";
		url = String.format("%s%s?eventId=%s&realName=%s", url, uri, eventId, realName);
//		System.out.printf("正在获取 %s 报警事件%n", apiUser);
		System.out.println(url);
		// 设置头部
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);

		// 发送POST请求并接收响应
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
		String responseBody = responseEntity.getBody();
//		System.out.println("responseBody:" + responseBody);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);
		//		System.out.println(new Date() + " 通知事件数:" + entities.size());

		return jsonNode.get("data").asInt();
	}

	/**
	 * @param eventId       事件ID
	 * @param resolveStatus 0:未处理 1:已处理 2：已忽略 3：未知
	 * @return 处理状态
	 */
	public static boolean resolveMonitorEvent(String eventId, String resolveStatus) throws IOException, ParserConfigurationException, SAXException {
		Map<String, Object> apiConnectConfig = getConfig();

		RestTemplate restTemplate = new RestTemplate();
		String url = String.format("http://%s:%s", apiConnectConfig.get("apiHost").toString(), apiConnectConfig.get("apiPort").toString());
		String uri = "/api/open/k-tss/resolve_monitor_event_status";
		url = String.format("%s%s?eventId=%s&resolveStatus=%s", url, uri, eventId, resolveStatus);
//		System.out.printf("正在获取 %s 报警事件%n", apiUser);
//		System.out.println(url);
		// 设置头部
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_JSON);

		// 发送POST请求并接收响应
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);
		String responseBody = responseEntity.getBody();
		System.out.println("responseBody:" + responseBody);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);
		//		System.out.println(new Date() + " 通知事件数:" + entities.size());

		return jsonNode.get("data").asInt()>0;
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
		return "动态监控报警通知";
	}


}
