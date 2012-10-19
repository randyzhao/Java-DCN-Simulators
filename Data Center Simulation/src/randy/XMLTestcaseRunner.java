/**  
 * Filename:    XMLTestcaseRunner.java  
 * Description:   
 * Copyright:   Copyright (c)2011 
 * Company:    company 
 * @author:     Hongze Zhao 
 * @version:    1.0  
 * Create at:   Oct 18, 2012 9:36:36 PM  
 *  
 * Modification History:  
 * Date         Author      Version     Description  
 * ------------------------------------------------------------------  
 * Oct 18, 2012    Hongze Zhao   1.0         1.0 Version  
 */
package randy;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Hongze Zhao
 * Create At : Oct 18, 2012 9:36:37 PM
 */
public class XMLTestcaseRunner {

	private String xmlPath;
	private Document doc = null;

	public XMLTestcaseRunner(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	/**
	 * run the test case
	 * 
	 * @author Hongze Zhao
	 */
	public void run() throws Exception {
		this.getXMLDocument();
		Element element = this.doc.getDocumentElement();
		System.out.println("Root element of the doc is "
				+ element.getNodeName());
		NodeList testcaseList = this.doc.getElementsByTagName("testcase");
		System.out.println("total number of testcase is "
				+ testcaseList.getLength());
		for (int i = 0; i < testcaseList.getLength(); i++) {
			this.processTestcase(testcaseList.item(i));
		}

	}

	private void getXMLDocument() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		this.doc = db.parse(new File(this.xmlPath));
	}

	private void processTestcase(Node testcase) {
		IDCN dcn = this.buildTopology(testcase);
		if (dcn != null) {
			this.runSimulator(testcase, dcn);
		}
	}

	private IDCN buildTopology(Node testcase) {
		Element nodeElement = (Element) testcase;
		NodeList topoNodeList = nodeElement.getElementsByTagName("topology");
		if (topoNodeList.getLength() != 1) {
			System.err.println("a testcase should contain one topology");
			return null;
		}
		Node topoNode = topoNodeList.item(0);// <topology> </topology>
		Node child = ((Element) topoNode).getFirstChild();
		if (child == null) {
			System.err.print("topology should not be empty");
			return null;
		}
		return this.parseDCN(child);
	}

	private IDCN parseDCN(Node dcnNode) {
		Element dcnElement = (Element) dcnNode;
		return XMLDCNBuilder.fromXMLElement(dcnElement);
	}

	private void runSimulator(Node testcase, IDCN dcn) {
		XMLSimulatorRunner.runFromXMLElement((Element) testcase, dcn);
	}
	/**
	 * @param args
	 * @author Hongze Zhao
	 */
	public static void main(String[] args) {
		try {
			XMLTestcaseRunner runner = new XMLTestcaseRunner(args[0]);
			runner.run();
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}

	}

}
