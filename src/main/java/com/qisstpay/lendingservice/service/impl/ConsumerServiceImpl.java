package com.qisstpay.lendingservice.service.impl;

import com.qisstpay.lendingservice.dto.tasdeeq.response.TasdeeqConsumerReportResponseDto;
import com.qisstpay.lendingservice.entity.Consumer;
import com.qisstpay.lendingservice.enums.GenderType;
import com.qisstpay.lendingservice.repository.ConsumerRepository;
import com.qisstpay.lendingservice.service.ConsumerService;
import com.qisstpay.lendingservice.utils.ModelConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    ConsumerRepository consumerRepository;

    @Autowired
    private ModelConverter modelConverter;

    private final String CALLING_CONSUMER_SERVICE = "Calling Consumer Service";

    @Override
    public Consumer getOrCreateConsumerDetails(TasdeeqConsumerReportResponseDto tasdeeqConsumerReportResponseDto, String phoneNumber) {
        log.info(CALLING_CONSUMER_SERVICE);
        log.info("getOrCreateConsumerDetails cnic: {} phoneNumber: {}", tasdeeqConsumerReportResponseDto.getPersonalInformation().getCnic(), phoneNumber);
        Optional<Consumer> consumer = findByCnicOrPhonenumber(tasdeeqConsumerReportResponseDto.getPersonalInformation().getCnic(), phoneNumber);
        Consumer newConsumer = modelConverter.convertToConsumer(tasdeeqConsumerReportResponseDto.getPersonalInformation());
        newConsumer.setPhoneNumber(phoneNumber);
        if (GenderType.MALE.getCode().equals(tasdeeqConsumerReportResponseDto.getPersonalInformation().getGender().toUpperCase(Locale.ROOT))) {
            newConsumer.setGender(GenderType.MALE);
        } else {
            newConsumer.setGender(GenderType.FEMALE);
        }
        newConsumer.setDateOfBirth(tasdeeqConsumerReportResponseDto.getPersonalInformation().getDob());
        if (consumer.isPresent()) {
            if (compareData(consumer.get(), newConsumer)) {
                return consumer.get();
            } else {
                consumer.get().setDeletedAt(Timestamp.valueOf(LocalDateTime.now()));
                save(consumer.get());
            }
        }
        return newConsumer;
    }

    @Override
    public Consumer save(Consumer consumer) {
        log.info(CALLING_CONSUMER_SERVICE);
        return consumerRepository.save(consumer);
    }

    @Override
    public Optional<Consumer> findByConsumerNumber(String consumerNumber) {
        return consumerRepository.findByPhoneNumber(consumerNumber);
    }

    @Override
    public Optional<Consumer> findByIdentityNumber(String identityNumber) {
        return consumerRepository.findByIdentityNumber(identityNumber);
    }

    public Optional<Consumer> findByCnicOrPhonenumber(String cnic, String phoneNumber) {
        List<Consumer> consumerList = consumerRepository.findByCnicOrPhoneNumber(cnic, phoneNumber);
        if (consumerList.size() > 1) {
            Optional<Consumer> consumer = consumerList.stream().filter(x -> (cnic.equals(x.getCnic()) && phoneNumber.equals(x.getPhoneNumber()))).findFirst();
            if (consumer.isPresent()) {
                return consumer;
            }
            consumer = consumerList.stream().filter(x -> cnic.equals(x.getCnic())).findFirst();
            if (consumer.isPresent()) {
                consumer.get().setPhoneNumber(phoneNumber);
                return consumer;
            }
            consumer = consumerList.stream().filter(x -> phoneNumber.equals(x.getPhoneNumber())).findFirst();
            if (consumer.isPresent()) {
                consumer.get().setCnic(cnic);
                return consumer;
            }
        } else if (consumerList.size() == 1) {
            if (consumerList.get(0).getPhoneNumber() == null || consumerList.get(0).getPhoneNumber().equals("")) {
                consumerList.get(0).setPhoneNumber(phoneNumber);
            }
            if (consumerList.get(0).getCnic() == null || consumerList.get(0).getCnic().equals("")) {
                consumerList.get(0).setCnic(cnic);
            }
            return Optional.ofNullable(consumerList.get(0));
        }
        return Optional.empty();
    }

    private Boolean compareData(Consumer consumer1, Consumer consumer2) {
        if (!consumer1.getCnic().equals(consumer2.getCnic())) {
            return Boolean.FALSE;
        } else if (!consumer1.getPhoneNumber().equals(consumer2.getPhoneNumber())) {
            return Boolean.FALSE;
        } else if (!consumer1.getName().equals(consumer2.getName())) {
            return Boolean.FALSE;
        } else if (!consumer1.getBorrowerType().equals(consumer2.getBorrowerType())) {
            return Boolean.FALSE;
        } else if (!consumer1.getGender().equals(consumer2.getGender())) {
            return Boolean.FALSE;
        } else if (!consumer1.getBusinessOrProfession().equals(consumer2.getBusinessOrProfession())) {
            return Boolean.FALSE;
        } else if (!consumer1.getPermanentAddress().equals(consumer2.getPermanentAddress())) {
            return Boolean.FALSE;
        } else if (!consumer1.getCurrentResidentialAddress().equals(consumer2.getCurrentResidentialAddress())) {
            return Boolean.FALSE;
        } else if (!consumer1.getEmployerOrBusiness().equals(consumer2.getEmployerOrBusiness())) {
            return Boolean.FALSE;
        } else if (!consumer1.getFatherOrHusbandName().equals(consumer2.getFatherOrHusbandName())) {
            return Boolean.FALSE;
        } else if (!consumer1.getNationality().equals(consumer2.getNationality())) {
            return Boolean.FALSE;
        } else if (!consumer1.getNic().equals(consumer2.getNic())) {
            return Boolean.FALSE;
        } else if (!consumer1.getPassport().equals(consumer2.getPassport())) {
            return Boolean.FALSE;
        } else if (!consumer1.getDateOfBirth().equals(consumer2.getDateOfBirth())) {
            return Boolean.FALSE;
        } else if (!consumer1.getNtn().equals(consumer2.getNtn())) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
