package br.com.pec.api.san.service;

import java.text.ParseException;
import java.util.regex.Pattern;

public final class CpfUtils {
    private static final Pattern ONLY_DIGITS = Pattern.compile("\\D");

    private CpfUtils() {}

    public static String digits(String cpf) {
        if (cpf == null) return null;
        return ONLY_DIGITS.matcher(cpf).replaceAll("");
    }

    public static String format(String cpf) {
        String d = digits(cpf);
        if (d == null || d.length() != 11) return cpf;
        return String.format("%s.%s.%s-%s", d.substring(0,3), d.substring(3,6), d.substring(6,9), d.substring(9));
    }

    public static boolean isValid(String cpf) {
        String d = digits(cpf);
        if (d == null || d.length() != 11) return false;
        if (d.chars().distinct().count() == 1) return false;
        int d1 = calcDigit(d.substring(0,9), 10);
        int d2 = calcDigit(d.substring(0,9) + d1, 11);
        return d.equals(d.substring(0,9) + d1 + d2);
    }

    private static int calcDigit(String base, int weightStart) {
        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            sum += Character.digit(base.charAt(i), 10) * (weightStart - i);
        }
        int mod = sum % 11;
        return (mod < 2) ? 0 : 11 - mod;
    }
}
