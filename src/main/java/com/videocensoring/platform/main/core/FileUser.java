/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.videocensoring.platform.main.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Kushan
 */
public class FileUser {
    
    private static Document doc;

    /**
     *
     * @param line
     * @param lastOne
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static final void saveXML(String line, boolean lastOne) throws TransformerConfigurationException, TransformerException, ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        final File file = new File("data.xml");
        if (!file.exists()) {
            file.createNewFile();
        }
        //System.out.println(file.exists());
        doc = docBuilder.parse(file);
        Element rootElement = doc.getDocumentElement();
        
        Element element = doc.createElement("data");
        
        element.appendChild(doc.createTextNode(line));
        rootElement.appendChild(element);
        
        DOMSource source = new DOMSource(doc);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StreamResult result = new StreamResult(new File("data.xml"));

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);
        transformer.transform(source, result);
        
    }
    
    public static final List<List<double[]>> getXML() throws ParserConfigurationException, SAXException, IOException {
        
        List<List<double[]>> exportList = new CopyOnWriteArrayList<>();
        
        File fXmlFile = new File("data.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        
        Element node = (Element) doc.getElementsByTagName("root").item(0);
        
        NodeList nList = node.getElementsByTagName("sample");

        //System.out.println(nList.getLength());
        for (int temp = 0; temp < nList.getLength(); temp++) {
            
            Node nNode = nList.item(temp);
            
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                List<double[]> list = new CopyOnWriteArrayList<>();
                Element eElement = (Element) nNode;
                System.out.println(eElement.getAttribute("ID"));
                NodeList childNodes = eElement.getChildNodes();
                
                for (int tempData = 0; tempData < childNodes.getLength(); tempData++) {
                    
                    Node nNodeData = childNodes.item(tempData);
                    
                    if (nNodeData.getNodeType() == Node.ELEMENT_NODE) {
                        
                        Node eElementData = nNodeData;
                        
                        final String textContent = eElementData.getTextContent();
                        //System.out.println(textContent.length());
                        if (textContent != null && !textContent.isEmpty()) {
                            list.add(stringToDoubleArray(textContent));
                        }
                    }
                }
                exportList.add(list);
            }
        }

        //System.out.println(list.size());
        return exportList;
    }
    
    private static double[] stringToDoubleArray(String values) {
        // System.out.println(values);
        StringTokenizer st = new StringTokenizer(values, ",");
        double[] arrayD = new double[st.countTokens()];
        int i = 0;
        while (st.hasMoreElements()) {
            
            Object object = st.nextElement();
            //      System.out.println(object);
            arrayD[i] = Double.parseDouble(object + "");
            //        System.out.println(arrayD[i]);
        }
        
        return arrayD;
    }
   
    
}
