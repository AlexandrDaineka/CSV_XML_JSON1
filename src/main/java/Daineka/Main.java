package Daineka;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Main {
    public static void main(String[] args) {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String csvFileName = "data.csv";
        List<Employee> csvList = parseCSV(columnMapping, csvFileName);
        String csvJson = listToJson(csvList);
        writeString(csvJson, "csvData.json");

        String xmlFileName = "data.xml";
        List<Employee> xmlList = parseXML(xmlFileName);
        String xmlJson = listToJson(xmlList);
        writeString(xmlJson, "xmlData.json");

    }

    private static List<Employee> parseCSV(String[] columnMapping, String csvFileName) {

        List<Employee> employeeList = null;

        try (CSVReader csvReader = new CSVReader(new FileReader(csvFileName))) {

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            employeeList = csvToBean.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return employeeList;

    }

    private static String listToJson(List<Employee> list) {

        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();;
        Gson gson = gsonBuilder.create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);

        return json;

    }

    private static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static List<Employee> parseXML(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fileName);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("employee");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    employees.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return employees;
    }

}