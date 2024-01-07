package com.borsaistanbul.stockvaluation.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String HOLDINGS = "Holding";

    // BALANCE SHEET CONSTANTS
    public static final String FINTABLES = "https://fintables.com/sirketler/{0}/finansal-tablolar/excel?currency=";
    public static final String INITIAL_CAPITAL = "Ödenmiş Sermaye";
    public static final String CASH_AND_EQUIVALENTS = "Nakit ve Nakit Benzerleri";
    public static final String FINANCIAL_LIABILITIES = "Finansal Borçlar";
    public static final String FINANCIAL_INVESTMENTS = "Finansal Yatırımlar";
    public static final String EQUITIES = "Ana Ortaklığa Ait Özkaynaklar";
    public static final String PARENT_COMPANY_SHARES = "Ana Ortaklık Payları";
    public static final String INCOME_FROM_SALES = "Satış Gelirleri";
    public static final String INCOME_FROM_OTHER_FIELDS = "Faiz, Ücret, Prim, Komisyon ve Diğer Gelirler";
    public static final String GROSS_PROFIT = "Brüt Kar (Zarar)";
    public static final String ADMINISTRATIVE_EXPENSES = "Genel Yönetim Giderleri (-)";
    public static final String MARKETING_SALES_DISTRIBUTION_EXPENSES = "Pazarlama, Satış ve Dağıtım Giderleri (-)";
    public static final String RESEARCH_DEVELOPMENT_EXPENSES = "Araştırma ve Geliştirme Giderleri (-)";
    public static final String AMORTIZATION = "Amortisman";
    public static final String TOTAL_ASSETS = "Toplam Varlıklar";
    public static final String TOTAL_SHORT_TERM_LIABILITIES = "Toplam Kısa Vadeli Yükümlülükler";
    public static final String TOTAL_LONG_TERM_LIABILITIES = "Toplam Uzun Vadeli Yükümlülükler";

    // UI CONSTANTS
    public static final String STRONG_BUY = "Güçlü Al";
    public static final String BUY = "Al";
    public static final String NEUTRAL = "Nötr";
    public static final String SELL = "Sat";
    public static final String STRONG_SELL = "Güçlü Sat";

}
