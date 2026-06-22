package com.shop.task;

import com.shop.model.Order;
import com.shop.model.OrderDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import java.io.FileInputStream;
import static org.junit.jupiter.api.Assertions.*;

class XmlGeneratorTest {

    private Order createOrder(int id, int userId, Date date, double total) {
        Order o = new Order();
        o.setId(id);
        o.setUserId(userId);
        o.setOrderDate(date);
        o.setTotalAmount(total);
        return o;
    }

    private OrderDetail createDetail(int id, int orderId, int productId, int quantity, double price) {
        OrderDetail d = new OrderDetail();
        d.setId(id);
        d.setOrderId(orderId);
        d.setProductId(productId);
        d.setQuantity(quantity);
        d.setPrice(price);
        return d;
    }

    @Test
    void testGenerateInvoice_createsXmlFile(@TempDir Path tempDir) {
        Order order = createOrder(1, 1, new Date(), 150000);
        List<OrderDetail> details = new ArrayList<>();
        details.add(createDetail(1, 1, 1, 2, 50000));
        details.add(createDetail(2, 1, 2, 1, 50000));

        String outputDir = tempDir.toString();
        String filePath = XmlGenerator.generateInvoice(order, details, outputDir);

        assertNotNull(filePath);
        File f = new File(filePath);
        assertTrue(f.exists());
        assertTrue(filePath.contains("invoice_1.xml"));
    }

    @Test
    void testGeneratedXml_hasCorrectStructure(@TempDir Path tempDir) throws Exception {
        Order order = createOrder(2, 1, new Date(), 99999);
        List<OrderDetail> details = new ArrayList<>();
        details.add(createDetail(1, 2, 3, 1, 99999));

        String filePath = XmlGenerator.generateInvoice(order, details, tempDir.toString());
        assertNotNull(filePath);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new FileInputStream(filePath));

        assertEquals("Invoice", doc.getDocumentElement().getNodeName());
        assertNotNull(doc.getElementsByTagName("Customer").item(0));
        assertNotNull(doc.getElementsByTagName("OrderDate").item(0));
        assertNotNull(doc.getElementsByTagName("LineItems").item(0));
        assertNotNull(doc.getElementsByTagName("TotalAmount").item(0));
        assertEquals("99999.0", doc.getElementsByTagName("TotalAmount").item(0).getTextContent());
    }

    @Test
    void testGeneratedXml_createsDirIfNotExists(@TempDir Path tempDir) {
        String nestedDir = tempDir.toString() + File.separator + "sub" + File.separator + "dir";
        Order order = createOrder(3, 1, new Date(), 5000);
        String filePath = XmlGenerator.generateInvoice(order, new ArrayList<>(), nestedDir);
        assertNotNull(filePath);
        assertTrue(new File(filePath).exists());
    }
}
