/**
 * Copyright (C) 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.formModeler.core.processing.formProcessing;

import org.apache.commons.logging.Log;
import org.jbpm.formModeler.core.config.FormManager;
import org.jbpm.formModeler.service.LocaleManager;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Util functions that can be used on field Formulas.
 */
@ApplicationScoped
public class Functions {

    @Inject
    private Log log;

    @Inject
    private FormManager formManager;

    private static int[] PESO_DIGITOS_CCC = {1, 2, 4, 8, 5, 10, 9, 7, 3, 6};
    private static String[] MONTHS = {"jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec"};

    /**
     * This enables using StringUtils functions by using something like Functions.String.replace(...)
     */
    public static final StringUtils String = new StringUtils();

    /**
     * This enables using WordUtils functions by using something like Functions.String.replace(...)
     */
    public static final WordUtils Word = new WordUtils();

    public Functions() {
    }

    // Extend this class providing desired project functions
    public boolean checkNIF(String dni) {
        if (StringUtils.isBlank(dni)) return false;
        // Make case insensitive
        String _dni= dni.toUpperCase();
        char[] letter = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X',
                'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
        char[] nif = new char[9];
        boolean startsWithLetter = false;
        String nifValue;

        if (_dni.length() < 8 || _dni.length() > 9) {
            return false;
        }
        if (_dni.length() == 9) {
            nif = _dni.toCharArray();
        } else {
            nif = new String("0" + _dni).toCharArray();
        }

        if (!Character.isLetter(nif[8])) {
            return false;
        }

        if (nif[0] == 'K' || nif[0] == 'L' || nif[0] == 'M' || nif[0] == 'X' || nif[0] == 'Y' || nif[0] == 'Z') {
            startsWithLetter = true;
        } else {
            if (!Character.isDigit(nif[0])) {
                return false;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (!Character.isDigit(nif[i])) {
                return false;
            }
        }

        if (startsWithLetter) {
            nifValue = _dni.substring(1, _dni.length() - 1);
        } else {
            nifValue = _dni.substring(0, _dni.length() - 1);
        }

        int control = Integer.parseInt(nifValue) % 23;
        return (nif[8] == letter[control]);
    }


    //IBAN         BANCO       OFICINA   CONTROL    NumCuenta
    // X X X X     X X X X     X X X X    X X    X X X X X X X X X X
    // 0 1 2 3     4 5 6 7     8 9 0 1    2 3    4 5 6 7 8 9 0 1 2 3
    public boolean checkIBAN(String numIBAN) {
        int DC1 = 0, DC2 = 0;

        if (numIBAN.length() != 24) return false;

        String idBancoOficina = numIBAN.substring(4, 12);
        String numCuenta = numIBAN.substring(14, 24);
        String CControl = numIBAN.substring(12, 14);
        String IBAN = numIBAN.substring(0, 4).toUpperCase();

        if (!idBancoOficina.matches("[0-9]{8}") || !numCuenta.matches("[0-9]{10}")
                || !CControl.matches("[0-9]{2}") || !IBAN.matches("ES([0-9]{2})")) return false;

        int digito;
        int x = 7;
        while (x >= 0) {
            digito = new Integer(idBancoOficina.substring(x, x + 1)).intValue();
            DC1 = DC1 + (PESO_DIGITOS_CCC[x + 2] * digito);
            x--;
        }
        DC1 = 11 - (DC1 % 11);
        if (DC1 == 10) DC1 = 1;
        if (DC1 == 11) DC1 = 0; // Digito control entidad-oficina

        x = 9;
        while (x >= 0) {
            digito = new Integer(numCuenta.substring(x, x + 1)).intValue();
            DC2 = DC2 + (PESO_DIGITOS_CCC[x] * digito);
            x--;
        }
        DC2 = 11 - (DC2 % 11);
        if (DC2 == 10) DC2 = 1;
        if (DC2 == 11) DC2 = 0; // Digito control numero C/C

        if (!CControl.equals(new Integer(DC1).toString() + new Integer(DC2).toString())) return false;

        /* Table mapeo letras a numeros para codigo pais, Solo implementado para IBAN Espa�ol
         A=10	G=16	M=22 	S=28	Y=34
         B=11	H=17	N=23 	T=29	Z=35
        C=12	I=18	O=24	U=30
        D=13	J=19	P=25	V=31
        E=14	K=20	Q=26	W=32
        F=15	L=21	R=27	X=33   */
        String numeroIban = IBAN.replaceAll("E", "14");
        numeroIban = numeroIban.replaceAll("S", "28");
        String calcIban = idBancoOficina + CControl + numCuenta + numeroIban;
        BigInteger result = new BigInteger(calcIban).mod(new BigInteger("97"));

        if (result.compareTo(new BigInteger("1")) != 0) return false;


        return true;
    }

    // CCC Example: 0072 0101 93 0000122351
    public boolean checkCCC(String CCC) {
        int DC1 = 0, DC2 = 0;

        if (CCC.length() != 20) return false;

        String idBancoOficina = CCC.substring(0, 8);
        String numCuenta = CCC.substring(10, 20);
        String CControl = CCC.substring(8, 10);


        if (!idBancoOficina.matches("[0-9]{8}") || !numCuenta.matches("[0-9]{10}")
                || !CControl.matches("[0-9]{2}")) return false;

        int digito;
        int x = 7;
        while (x >= 0) {
            digito = new Integer(idBancoOficina.substring(x, x + 1)).intValue();
            DC1 = DC1 + (PESO_DIGITOS_CCC[x + 2] * digito);
            x--;
        }
        DC1 = 11 - (DC1 % 11);
        if (DC1 == 10) DC1 = 1;
        if (DC1 == 11) DC1 = 0; // Digito control entidad-oficina

        x = 9;
        while (x >= 0) {
            digito = new Integer(numCuenta.substring(x, x + 1)).intValue();
            DC2 = DC2 + (PESO_DIGITOS_CCC[x] * digito);
            x--;
        }
        DC2 = 11 - (DC2 % 11);
        if (DC2 == 10) DC2 = 1;
        if (DC2 == 11) DC2 = 0; // Digito control numero C/C

        if (!CControl.equals(new Integer(DC1).toString() + new Integer(DC2).toString())) return false;

        return true;
    }

    // CIF Example: A58818501
    public boolean checkCIF(String CIF) {
        if (CIF == null) return true;

        CIF = CIF.toUpperCase();
        Pattern cifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
        if (!cifPattern.matcher(CIF).matches()) return false;

        int parA = 0;
        for (int i = 2; i < 8; i += 2) {
            int digito = Character.digit(CIF.charAt(i), 10);
            if (digito < 0) return false;

            parA += digito;
        }

        int nonB = 0;
        for (int i = 1; i < 9; i += 2) {
            int digito = Character.digit(CIF.charAt(i), 10);
            if (digito < 0) return false;

            int nn = 2 * digito;
            if (nn > 9) nn = 1 + (nn - 10);
            nonB += nn;
        }

        int parcialC = parA + nonB;
        int digitoE = parcialC % 10;
        int digitoD = (digitoE > 0) ? (10 - digitoE) : 0;
        char letraIni = CIF.charAt(0);
        char caracterFin = CIF.charAt(8);

        String CONTROL_SOLO_NUMEROS = "ABEH"; // Sólo admiten números como caracter de control
        String CONTROL_SOLO_LETRAS = "KPQS"; // Sólo admiten letras como caracter de control
        String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI"; // Conversión de dígito a letra de control.
        boolean letterOk = (CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0 && CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin);
        boolean digitOk = (CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
        return letterOk || digitOk;
    }

    public int yearsOld(Date birthDate) throws ParseException {
        GregorianCalendar bd = new GregorianCalendar();
        bd.clear();
        bd.setTime(birthDate);
        GregorianCalendar td = new GregorianCalendar();
        td.clear();
        td.setTime(new Date());
        int i = -1;
        while (td.after(bd)) {
            i++;
            bd.add(Calendar.YEAR, 1);
        }
        return i;
    }

    public Map getYearsBetween(int min, int max) throws Exception {
        if (max < min) throw new Exception("Error getting years bewtween " + min + " - " + max + ".");
        Map years = new TreeMap();

        int year = new GregorianCalendar().get(GregorianCalendar.YEAR);

        for (int i = year + min; i <= year + max; i++) {

            String value = java.lang.String.valueOf(i);
            years.put(value, value);
        }
        return years;
    }

    public Map getMonths() {
        Map months = new TreeMap();

        ResourceBundle bundle = ResourceBundle.getBundle("org.jbpm.formModeler.core.processing.formProcessing.messages", LocaleManager.currentLocale());
        for (int i = 0; i < MONTHS.length; i++) {
            String key = java.lang.String.valueOf(i);
            if (key.length() == 1) key = "0" + key;
            months.put(key, bundle.getString("months." + MONTHS[i]));
        }

        return months;
    }

    public Map getValidDays() {
        return getValidDays(null);
    }

    public Map getValidDays(String value) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.DAY_OF_MONTH, 1);

        if (value == null || value.equals("") || value.startsWith("/")) {
            gc.set(GregorianCalendar.MONTH, 0);
        } else if (value.endsWith("/")) {
            int month = Integer.decode(value.substring(0, value.indexOf("/"))).intValue();
            gc.set(GregorianCalendar.MONTH, month);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
            try {
                gc.setTime(sdf.parse(value));
                gc.set(GregorianCalendar.MONTH, gc.get(GregorianCalendar.MONTH) + 1);
            } catch (Exception e) {
                log.warn("Error parsing date " + value + " : ", e);
            }
        }

        Map days = new TreeMap();

        int month = gc.get(GregorianCalendar.MONTH);
        while (gc.get(GregorianCalendar.MONTH) == month) {
            int intValue = gc.get(GregorianCalendar.DAY_OF_MONTH);
            String key = java.lang.String.valueOf(intValue);
            if (key.length() == 1) key = "0" + key;
            days.put(key, key);
            gc.set(GregorianCalendar.DAY_OF_MONTH, intValue + 1);
        }

        return days;
    }

    public Map getValidDays(String sMonth, String sYear) {

        int month = Integer.decode(sMonth).intValue();
        int year = Integer.decode(sYear).intValue();
        GregorianCalendar gc = new GregorianCalendar(year, month, 1);

        Map days = new HashMap();

        while (gc.get(GregorianCalendar.MONTH) == month) {
            Integer value = new Integer(gc.get(GregorianCalendar.DAY_OF_MONTH));
            days.put(value, value.toString());
            gc.set(GregorianCalendar.DAY_OF_MONTH, value.intValue() + 1);
        }

        return days;
    }

    public Date getDateFromFields(String sDay, String sMonth, String sYear) {
        int day = Integer.decode(sDay).intValue();
        int month = Integer.decode(sMonth).intValue();
        int year = Integer.decode(sYear).intValue();
        GregorianCalendar gc = new GregorianCalendar(year, month, day);
        return gc.getTime();
    }


    /**
     * Return an empty string. This method, in combination with str(s) serve as default string inside formulas.
     *
     * @return an empty string
     */
    public String str() {
        return "";
    }

    /**
     * String given as argument
     *
     * @param s string to return
     * @return String given as argument
     */
    public String str(String s) {
        return s;
    }

    /**
     * Returns a color depending on the supplied values.
     *
     * @param value value to consider
     * @param low   lower limit
     * @param high  upper limit
     * @return green, orange or red, depending on the
     */
    public String semaphor(Number value, Number low, Number high) {
        if (value.doubleValue() < low.doubleValue()) return "green";
        if (value.doubleValue() > high.doubleValue()) return "red";
        return "orange";
    }


}




