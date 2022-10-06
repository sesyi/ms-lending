package com.qisstpay.lendingservice.config.cache;

import java.util.HashMap;

public class CacheUpdate {

    private HashMap<String, Long> entryTime = new HashMap<>();

    public boolean useCacheValue(String key, long timeInHours){


        boolean addValueToCache = entryTime.get(key)==null || entryTime.get(key) + timeInHours*3600000 > System.currentTimeMillis();//*60*60*1000;

        if(entryTime.get(key)==null || addValueToCache)
            entryTime.put(key, System.currentTimeMillis());

        return addValueToCache;
    }

    public String getKey(String prefix, String... args){
        StringBuilder stringBuilder = new StringBuilder(prefix);
        for (String arg : args) {
            stringBuilder.append(":").append(arg);
        }
        return stringBuilder.toString();
    }

}
