package com.bob.autonotify;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class TestTmp {
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
		// 创建DocumentBuilderFactory实例
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		// 创建DocumentBuilder实例
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		// 解析当前目录下的XML文件
		Document doc = dBuilder.parse(new File("src/main/resources/AutoNotifyConfig.xml"));
		// 正常化文档，以便更容易地读取
		doc.getDocumentElement().normalize();
		// 初始化参数

		String apiHost = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("HOST").item(0).getTextContent();
		String apiPort = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("PORT").item(0).getTextContent();
		String apiUser = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("USER").item(0).getTextContent();
		String apiToken = ((Element) doc.getElementsByTagName("API").item(0)).getElementsByTagName("TOKEN").item(0).getTextContent();
//		String title = ((Element) doc.getElementsByTagName("NOTIFY").item(0)).getElementsByTagName("TITLE").item(0).getTextContent();
	}
}
