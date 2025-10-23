package br.com.pec.api.util;

public final class CpfUtils {
    private CpfUtils() {}

    public static String onlyDigits(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    public static String format(String cpf) {
        String digits = onlyDigits(cpf);
        if (digits == null || digits.length() != 11) {
            throw new IllegalArgumentException("CPF inválido. Deve conter 11 dígitos.");
        }
        return String.format("%s.%s.%s-%s",
                digits.substring(0,3),
                digits.substring(3,6),
                digits.substring(6,9),
                digits.substring(9));
    }
}
