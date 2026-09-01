package com.carlikeafriend_backend.backend.util;

public class StringUtils {

    // 1. Limpia espacios y pone la primera letra de cada palabra en mayúscula (Nombres, Apellidos, Ciudades)
    public static String capitalize(String text){
        if(text == null || text.isBlank()) return null;

        String [] words = text.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();

        for(String word : words){
            if(!word.isEmpty()){
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    // 2. Limpia espacios y asegura minúsculas (Emails)
    public static String normalizeToLowerCase(String text){
        if(text == null || text.isBlank()) return null;
        return text.trim().toLowerCase();
    }

    // 3. Limpia espacios y asegura mayúsculas (Placas, VIN, Códigos de País)
    public static String normalizeToUpperCase(String text){
        if(text == null || text.isBlank()) return null;
        return text.trim().toUpperCase();
    }

}
