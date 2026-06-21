package com.lumetix.core;

import java.io.*;
import java.util.Base64;

public class SerializationUtil {


    public static <T extends Serializable> String serializeToString(T obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(obj);
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception ignoreE) {
            throw new RuntimeException(ignoreE);
        }
    }

    public static <T extends Serializable> T deserializeFromString(String str) {
        try {
            byte[] bytes = Base64.getDecoder().decode(str);
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object o = ois.readObject();
                return (T) o;
            }
        } catch (Exception ignore) {
            throw new RuntimeException(ignore);
        }
    }
}
