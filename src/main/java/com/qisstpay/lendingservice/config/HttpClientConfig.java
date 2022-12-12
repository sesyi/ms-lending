package com.qisstpay.lendingservice.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
@Slf4j
public class HttpClientConfig {

    @Value("${environment}")
    private String environment;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean(name = "restTemplateWithoutSSL")
//    public RestTemplate restTemplateWithoutSSL() {
//
//        if(!environment.equals("PROD")){
//            return new RestTemplate();
//        }
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        SSLContext sslcontext = null;
//        try {
//            sslcontext = SSLContexts.custom() .loadTrustMaterial(null, (chain, authType) -> true) .build();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            log.error("Error in Configuring restTemplateWithoutSSL bean");
//            return null;
//        }
//        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, new NoopHostnameVerifier());
//        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
//        requestFactory.setHttpClient(httpClient);
//
//        log.info("Configured restTemplateWithoutSSL bean");
//        return new RestTemplate(requestFactory);
//    }

    @Bean(name = "restTemplateWithoutSSL")
    public RestTemplate restTemplateWithoutSSL() {

        if(!environment.equals("PROD")){
            log.error("Configured restTemplate with SSL");
            return new RestTemplate();
        }

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in Configuring restTemplateWithoutSSL bean");
            return null;
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        log.info("Configured restTemplateWithoutSSL bean");

        return new RestTemplate(requestFactory);
    }
}
