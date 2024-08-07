package com.example.springbatch.batch;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.batch.item.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class ExcelRowReader implements ItemStreamReader<Row> {

    private final String filePath;      // 엑셀 파일 경로
    private FileInputStream fileInputStream;    // 파일을 열 객체
    private Workbook workbook;      // 엑셀 파일을 받을 객체
    private Iterator<Row> rowCursor;    // 엑셀 시트 행을 반복할 커서
    private int currentRowNumber;       // 어디까지 수행 되었는지 행 번호를 기록
    private final String CURRENT_ROW_KEY = "current.row.number";        // 배치 메타데이터 DB에 어떤 행까지 수행했는지 기록하기 위한 키

    public ExcelRowReader(String filePath) throws IOException {
        this.filePath = filePath;
        this.currentRowNumber = 0;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        try {
            fileInputStream = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            this.rowCursor = sheet.iterator();

            // 동일 배치 파라미터에 대해 특정 키 값 "current.row.number"의 값이 존재한다면 초기화
            if (executionContext.containsKey(CURRENT_ROW_KEY)) {
                currentRowNumber = executionContext.getInt(CURRENT_ROW_KEY);
            }

            // 위의 값을 가져와 이미 실행한 부분은 건너 뜀
            for (int i = 0; i < currentRowNumber && rowCursor.hasNext(); i++) {
                rowCursor.next();
            }

        } catch (IOException e) {
            throw new ItemStreamException(e);
        }
    }

    @Override
    public Row read() {

        if (rowCursor != null && rowCursor.hasNext()) {
            currentRowNumber++;
            return rowCursor.next();
        } else {
            return null;
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT_ROW_KEY, currentRowNumber);
    }

    @Override
    public void close() throws ItemStreamException {

        try {
            if (workbook != null) {
                workbook.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (IOException e) {
            throw new ItemStreamException(e);
        }
    }
}
