package com.qisstpay.lendingservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommonUtility {

    private static final String regexHtmlTag = "\\<.*?\\>";

    public static String getObjectJson(final Object o) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    public static String encodeValue(final String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static JsonObject convertHtmlIntoJson(String htmlText) {
        JsonObject object = new JsonObject();
        final JsonArray[] array = {new JsonArray()};

        final String[] property = {null};
        if (htmlText != null) {
            Document doc = Jsoup.parse(htmlText);
            Elements para = doc.getElementsByTag("p");
            Elements strong = doc.getElementsByTag("strong");
            List<String> propertyList = new ArrayList<>();
            strong.forEach(element -> propertyList.add(element.text()));
            para.forEach(element -> {
                if (propertyList.contains(element.text())) {
                    if (property[0] != null) {
                        object.add(property[0], array[0]);
                    }
                    property[0] = element.text();
                    array[0] = new JsonArray();
                } else if (element.toString().contains("<br>")) {
                    String[] valueList = element.toString().split("<br>");
                    for (String i : valueList) {
                        String value = i.replaceAll(regexHtmlTag, "");
                        if (propertyList.contains(value)) {
                            if (property[0] != null) {
                                object.add(property[0], array[0]);
                            }
                            property[0] = value;
                            array[0] = new JsonArray();
                        } else {
                            array[0].add(value);
                        }
                    }
                } else {
                    array[0].add(element.text());
                }
            });
            if (property[0] != null) {
                object.add(property[0], array[0]);
            }
        }
        return object;
    }


//    String description = null;
//    String regex = "\\<.*?\\>";
//    String breakTag ="<br>";
//            if (product.getDescription() != null) {
//        if (product.getDescription().contains("\n")) {
//            description = product.getDescription().replaceAll(regex, "");
//        } else {
//            description = product.getDescription().replaceAll(breakTag, "\n");
//            description = description.replaceAll(regex, "");
//        }
//    }

}
