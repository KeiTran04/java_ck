package com.shop.task;

import com.shop.dto.Invoice;
import com.shop.model.Order;
import com.shop.model.OrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class XmlGenerator {

    private static final Logger log = LoggerFactory.getLogger(XmlGenerator.class);

    public static String generateInvoice(Order order, List<OrderDetail> details, String outputDir) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            List<Invoice.LineItem> items = details.stream()
                .map(d -> new Invoice.LineItem(d.getProductId(), d.getQuantity(), d.getPrice()))
                .collect(Collectors.toList());

            Invoice invoice = new Invoice(
                order.getUserId(),
                sdf.format(order.getOrderDate()),
                items,
                order.getTotalAmount()
            );

            File dir = new File(outputDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = outputDir + File.separator + "invoice_" + order.getId() + ".xml";

            JAXBContext context = JAXBContext.newInstance(Invoice.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(invoice, new File(filePath));

            log.info("Invoice generated: {}", filePath);
            return filePath;

        } catch (Exception e) {
            log.error("generateInvoice failed", e);
            return null;
        }
    }
}
