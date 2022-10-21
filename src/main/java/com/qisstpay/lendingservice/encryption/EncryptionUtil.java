package com.qisstpay.lendingservice.encryption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    @Value("${ep.encryption.file.path}")
    private String epEncryptionFilePath;

    public String getEncryptedPayload (String valueToEncrypt) {
//        inputEnc = "1010@923376246667~2022-10-17 09:52:54.61487";
//          inputEnc = "AAIkNjE3ODVjMGItYmRjNi00YTlhLTlhODctODJhZDBjMjc1N2Ji3pLAhnDS2v4-O6dnl5rT2AhavjxrRtISnhaSGgUCaU7uHEEhV7w9smbgrjGUW_Z2oMPqG2ydffxoO0KUVbnSgbAXrGUBJS1Yel3QB6sRFYmZMi-5TjeaCAg-oRZ_cDTW";
//        inputEnc = "https://webhook.site/ceaa39f2-6e0c-4298-8686-aa7ed92fbc9f";
        try {
            String outputEnc = Encryption.encrypt(valueToEncrypt, Encryption.getPublicKey(epEncryptionFilePath));
//            String outputEnc = Encryption.encrypt(inputEnc, Encryption.getPublicKey("C:\\Users\\hp\\Desktop\\Keys\\publickey.der"));
            System.out.println(outputEnc);
            System.out.println();
            return outputEnc;
            //System.out.println(Encryption.decrypt("AAIkNjE3ODVjMGItYmRjNi00YTlhLTlhODctODJhZDBjMjc1N2JiBMLJGqL4qJJ4LF_izawQj2G3CHCOnzNZbtiF9EL9p-bgVDCBY7deBiOvTAoYpB0k-JDekVRq0GTTQd-yxzf6f_1K7X_nLoytO8fFctrHQgqyxbpvRUA0p88kx5jaajkj", Encryption.getPrivateKey("D:\\AES keys\\publickey.der")));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "Encryption Failed";
        }
    }
}
