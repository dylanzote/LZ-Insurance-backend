package com.zote.common.utils.files;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@UtilityClass
public class CsvUtils {
    
    private static final CSVFormat BASE_DEFAULT_FORMAT = CSVFormat.Builder.create()
            .setQuote('"')
            .setRecordSeparator("\n")
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();

    private static final CSVFormat BASE_EXCEL_FORMAT = CSVFormat.Builder.create(CSVFormat.EXCEL)
            .setQuote('"')
            .setRecordSeparator("\r\n")
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();

    // Public formats with headers
    public static final CSVFormat DEFAULT_CSV_FORMAT = BASE_DEFAULT_FORMAT;
    
    public static final CSVFormat EXCEL_CSV_FORMAT = BASE_EXCEL_FORMAT;
    
    public static final CSVFormat TDF_FORMAT = CSVFormat.Builder.create(CSVFormat.TDF)
            .setQuote('"')
            .setRecordSeparator("\n")
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();
    
    public static final CSVFormat RFC4180_FORMAT = CSVFormat.Builder.create(CSVFormat.RFC4180)
            .setQuote('"')
            .setRecordSeparator("\n")
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();
    
    // Parser formats
    public static final CSVFormat PARSER_FORMAT_WITH_HEADERS = CSVFormat.Builder.create()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setQuote('"')
            .setRecordSeparator("\n")
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();
    
    public static final CSVFormat PARSER_FORMAT_NO_HEADERS = CSVFormat.Builder.create()
            .setQuote('"')
            .setRecordSeparator("\n")
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();

    /**
     * Generic CSV generation from objects with field extractors
     */
    public <T> String generateCsv(List<T> data, String[] headers, List<Function<T, String>> fieldExtractors) {
        return generateCsv(data, headers, fieldExtractors, DEFAULT_CSV_FORMAT);
    }
    
    public <T> String generateCsv(List<T> data, String[] headers, List<Function<T, String>> fieldExtractors, CSVFormat baseFormat) {
        
        validateInput(headers, fieldExtractors);
        
        CSVFormat formatWithHeaders = CSVFormat.Builder.create(baseFormat)
                .setHeader(headers)
                .get();

        try (StringWriter writer = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, formatWithHeaders)) {
            
            for (T item : data) {
                Object[] rowValues = extractRowValues(item, fieldExtractors);
                csvPrinter.printRecord(rowValues);
            }
            
            csvPrinter.flush();
            return writer.toString();
            
        } catch (IOException e) {
            log.error("Failed to generate CSV", e);
            throw new CsvGenerationException("Failed to generate CSV", e);
        }
    }
    
    /**
     * Generate CSV from Maps
     */
    public String generateCsvFromMaps(List<Map<String, String>> data, String[] headers) {
        return generateCsvFromMaps(data, headers, DEFAULT_CSV_FORMAT);
    }
    
    public String generateCsvFromMaps(List<Map<String, String>> data, String[] headers, CSVFormat baseFormat) {
        validateHeaders(headers);
        
        CSVFormat formatWithHeaders = CSVFormat.Builder.create(baseFormat)
                .setHeader(headers)
                .get();

        try (StringWriter writer = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, formatWithHeaders)) {
            
            for (Map<String, String> row : data) {
                Object[] rowValues = new Object[headers.length];
                for (int i = 0; i < headers.length; i++) {
                    rowValues[i] = row.getOrDefault(headers[i], "");
                }
                csvPrinter.printRecord(rowValues);
            }
            
            csvPrinter.flush();
            return writer.toString();
            
        } catch (IOException e) {
            log.error("Failed to generate CSV from maps", e);
            throw new CsvGenerationException("Failed to generate CSV from maps", e);
        }
    }
    
    /**
     * Generate CSV from String arrays
     */
    public String generateCsvFromArrays(List<String[]> data, String[] headers) {
        return generateCsvFromArrays(data, headers, DEFAULT_CSV_FORMAT);
    }
    
    public String generateCsvFromArrays(List<String[]> data, String[] headers, CSVFormat baseFormat) {
        validateHeaders(headers);
        
        CSVFormat formatWithHeaders = CSVFormat.Builder.create(baseFormat)
                .setHeader(headers)
                .get();

        try (StringWriter writer = new StringWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, formatWithHeaders)) {
            
            for (String[] record : data) {
                csvPrinter.printRecord((Object[]) record);
            }
            
            csvPrinter.flush();
            return writer.toString();
            
        } catch (IOException e) {
            log.error("Failed to generate CSV from arrays", e);
            throw new CsvGenerationException("Failed to generate CSV from arrays", e);
        }
    }
    
    /**
     * Generate CSV bytes with UTF-8 BOM for Excel
     */
    public <T> byte[] generateCsvBytesWithBom(List<T> data, String[] headers, List<Function<T, String>> fieldExtractors) {
        String csv = generateCsv(data, headers, fieldExtractors, EXCEL_CSV_FORMAT);
        return addUtf8Bom(csv);
    }
    
    public byte[] generateCsvBytesWithBomFromMaps(List<Map<String, String>> data, String[] headers) {
        String csv = generateCsvFromMaps(data, headers, EXCEL_CSV_FORMAT);
        return addUtf8Bom(csv);
    }
    
    public byte[] generateCsvBytesWithBomFromArrays(List<String[]> data, String[] headers) {
        String csv = generateCsvFromArrays(data, headers, EXCEL_CSV_FORMAT);
        return addUtf8Bom(csv);
    }
    
    /**
     * Parse CSV to list of maps (with headers)
     */
    public List<Map<String, String>> parseCsvToMaps(String csvContent) throws IOException {
        return parseCsvToMaps(csvContent, PARSER_FORMAT_WITH_HEADERS);
    }
    
    public List<Map<String, String>> parseCsvToMaps(String csvContent, CSVFormat parserFormat) throws IOException {
        try (Reader reader = new java.io.StringReader(csvContent)) {
            return parseCsvToMaps(reader, parserFormat);
        }
    }
    
    public List<Map<String, String>> parseCsvToMaps(InputStream inputStream) throws IOException {
        return parseCsvToMaps(inputStream, PARSER_FORMAT_WITH_HEADERS);
    }
    
    public List<Map<String, String>> parseCsvToMaps(InputStream inputStream, CSVFormat parserFormat) throws IOException {
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return parseCsvToMaps(reader, parserFormat);
        }
    }
    
    public List<Map<String, String>> parseCsvToMaps(Reader reader, CSVFormat parserFormat) throws IOException {
        List<Map<String, String>> records = new ArrayList<>();
        
        try (org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
                reader, parserFormat)) {
            
            List<String> headerNames = parser.getHeaderNames();
            
            for (CSVRecord record : parser) {
                Map<String, String> recordMap = new LinkedHashMap<>();
                for (String header : headerNames) {
                    recordMap.put(header, record.get(header));
                }
                records.add(recordMap);
            }
        }
        
        return records;
    }
    
    /**
     * Parse CSV to list of string arrays (no headers)
     */
    public List<String[]> parseCsvToArrays(String csvContent) throws IOException {
        return parseCsvToArrays(csvContent, PARSER_FORMAT_NO_HEADERS);
    }
    
    public List<String[]> parseCsvToArrays(String csvContent, CSVFormat parserFormat) throws IOException {
        try (Reader reader = new java.io.StringReader(csvContent)) {
            return parseCsvToArrays(reader, parserFormat);
        }
    }
    
    public List<String[]> parseCsvToArrays(Reader reader, CSVFormat parserFormat) throws IOException {
        List<String[]> records = new ArrayList<>();
        
        try (org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
                reader, parserFormat)) {
            
            for (CSVRecord record : parser) {
                List<String> values = new ArrayList<>();
                for (String value : record) {
                    values.add(value);
                }
                records.add(values.toArray(new String[0]));
            }
        }
        
        return records;
    }
    
    /**
     * Add UTF-8 BOM for Excel compatibility
     */
    public byte[] addUtf8Bom(String content) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Writer writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            
            writer.write('\ufeff');
            writer.write(content);
            writer.flush();
            
            return baos.toByteArray();
            
        } catch (IOException e) {
            log.error("Failed to add UTF-8 BOM", e);
            throw new CsvGenerationException("Failed to add UTF-8 BOM", e);
        }
    }
    
    /**
     * Create InputStream from CSV with BOM
     */
    public InputStream createCsvInputStreamWithBom(List<String[]> data, String[] headers) {
        String csv = generateCsvFromArrays(data, headers, EXCEL_CSV_FORMAT);
        byte[] csvBytes = addUtf8Bom(csv);
        return new ByteArrayInputStream(csvBytes);
    }
    
    /**
     * Utility methods
     */
    public boolean isValidCsv(String csvContent) {
        try (Reader reader = new java.io.StringReader(csvContent)) {
            org.apache.commons.csv.CSVParser.parse(reader, PARSER_FORMAT_NO_HEADERS);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public int countCsvRecords(String csvContent) throws IOException {
        try (Reader reader = new java.io.StringReader(csvContent)) {
            return countCsvRecords(reader, PARSER_FORMAT_NO_HEADERS);
        }
    }
    
    public int countCsvRecords(Reader reader, CSVFormat parserFormat) throws IOException {
        int count = 0;
        try (org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
                reader, parserFormat)) {
            for (CSVRecord ignored : parser) {
                count++;
            }
        }
        return count;
    }
    
    // Private helper methods
    
    private <T> void validateInput(String[] headers, List<Function<T, String>> fieldExtractors) {
        if (headers == null || fieldExtractors == null) {
            throw new IllegalArgumentException("Headers and field extractors cannot be null");
        }
        
        if (headers.length != fieldExtractors.size()) {
            throw new IllegalArgumentException("Number of headers must match number of field extractors");
        }
    }
    
    private void validateHeaders(String[] headers) {
        if (headers == null) {
            throw new IllegalArgumentException("Headers cannot be null");
        }
    }
    
    private <T> Object[] extractRowValues(T item, List<Function<T, String>> fieldExtractors) {
        Object[] values = new Object[fieldExtractors.size()];
        
        for (int i = 0; i < fieldExtractors.size(); i++) {
            Function<T, String> extractor = fieldExtractors.get(i);
            String value = extractor.apply(item);
            values[i] = value != null ? value : "";
        }
        
        return values;
    }
    
    /**
     * Fluent CSV Builder
     */
    public static class CsvBuilder<T> {
        private List<T> data = new ArrayList<>();
        private String[] headers;
        private List<Function<T, String>> fieldExtractors;
        private CSVFormat baseFormat = EXCEL_CSV_FORMAT;
        private boolean includeBom = false;
        private boolean includeHeaders = true;
        
        public CsvBuilder<T> withData(List<T> data) {
            if (data != null) {
                this.data = data;
            }
            return this;
        }
        
        public CsvBuilder<T> withHeaders(String... headers) {
            this.headers = headers;
            return this;
        }
        
        public CsvBuilder<T> withFieldExtractors(List<Function<T, String>> fieldExtractors) {
            this.fieldExtractors = fieldExtractors;
            return this;
        }
        
        public CsvBuilder<T> withFormat(CSVFormat baseFormat) {
            this.baseFormat = baseFormat;
            return this;
        }
        
        public CsvBuilder<T> withExcelFormat() {
            this.baseFormat = EXCEL_CSV_FORMAT;
            return this;
        }
        
        public CsvBuilder<T> withDefaultFormat() {
            this.baseFormat = DEFAULT_CSV_FORMAT;
            return this;
        }
        
        public CsvBuilder<T> withTdfFormat() {
            this.baseFormat = TDF_FORMAT;
            return this;
        }
        
        public CsvBuilder<T> withQuoteMode(QuoteMode quoteMode) {
            this.baseFormat = CSVFormat.Builder.create(baseFormat)
                    .setQuoteMode(quoteMode)
                    .get();
            return this;
        }
        
        public CsvBuilder<T> withDelimiter(char delimiter) {
            this.baseFormat = CSVFormat.Builder.create(baseFormat)
                    .setDelimiter(delimiter)
                    .get();
            return this;
        }
        
        public CsvBuilder<T> withQuoteChar(char quoteChar) {
            this.baseFormat = CSVFormat.Builder.create(baseFormat)
                    .setQuote(quoteChar)
                    .get();
            return this;
        }
        
        public CsvBuilder<T> withRecordSeparator(String recordSeparator) {
            this.baseFormat = CSVFormat.Builder.create(baseFormat)
                    .setRecordSeparator(recordSeparator)
                    .get();
            return this;
        }
        
        public CsvBuilder<T> withBom(boolean includeBom) {
            this.includeBom = includeBom;
            return this;
        }
        
        public CsvBuilder<T> includeHeaders(boolean includeHeaders) {
            this.includeHeaders = includeHeaders;
            return this;
        }
        
        public String buildString() {
            if (!includeHeaders || headers == null) {
                // Generate without headers
                return buildWithoutHeaders();
            }
            return CsvUtils.generateCsv(data, headers, fieldExtractors, baseFormat);
        }
        
        private String buildWithoutHeaders() {
            try (StringWriter writer = new StringWriter();
                 CSVPrinter csvPrinter = new CSVPrinter(writer, baseFormat)) {
                
                for (T item : data) {
                    Object[] rowValues = extractRowValues(item, fieldExtractors);
                    csvPrinter.printRecord(rowValues);
                }
                
                csvPrinter.flush();
                return writer.toString();
                
            } catch (IOException e) {
                log.error("Failed to generate CSV without headers", e);
                throw new CsvGenerationException("Failed to generate CSV without headers", e);
            }
        }
        
        public byte[] buildBytes() {
            String csv = buildString();
            if (includeBom) {
                return CsvUtils.addUtf8Bom(csv);
            }
            return csv.getBytes(StandardCharsets.UTF_8);
        }
        
        public InputStream buildInputStream() {
            byte[] bytes = buildBytes();
            return new ByteArrayInputStream(bytes);
        }
        
        public int getRecordCount() {
            return data.size();
        }
    }
    
    /**
     * Create CSV builder
     */
    public static <T> CsvBuilder<T> builder() {
        return new CsvBuilder<>();
    }
    
    /**
     * CSV Generation Exception
     */
    public static class CsvGenerationException extends RuntimeException {
        public CsvGenerationException(String message) {
            super(message);
        }
        
        public CsvGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}