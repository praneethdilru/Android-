package com.theempires.jetshop.manage;
import java.util.UUID;
public class UserUtils {
    public static String generateUniqueUserId() {

        UUID uuid = UUID.randomUUID();


        String userId = uuid.toString().replace("-", "");


        return userId;
    }
}
