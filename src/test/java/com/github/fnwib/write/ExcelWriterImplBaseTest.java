package com.github.fnwib.write;

import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.util.UUIDUtils;
import model.EnumType;
import model.WriteModel;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelWriterImplBaseTest {

    File tempTemplateFile;

    File exportFolder;

    @Before
    public void initDate() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String dir = classLoader.getResource("test-file").getFile();
        System.out.println(dir);
        tempTemplateFile = new File(dir + File.separator + "write-template.xlsx");

        String path = dir + File.separator + UUIDUtils.getHalfId();
        exportFolder = new File(path);
        FileUtils.forceMkdir(exportFolder);
        String filenameText = path + File.separator + UUIDUtils.getHalfId() + ".text";
        boolean newFile = new File(filenameText).createNewFile();
        if (!newFile) {
            throw new IOException("can not create new file" + filenameText);
        }
    }

    @After
    public void deleteData() throws IOException {
        FileUtils.forceDelete(exportFolder);
    }

    List<WriteModel> getDataList(int length) {
        List<WriteModel> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {

            Map<Integer, String> mapNumber = new HashMap<>();
            mapNumber.put(4, "Map1");
            mapNumber.put(5, "Map2");
            mapNumber.put(6, "Map3");
            Map<Integer, String> mapString = new HashMap<>();
            mapString.put(7, "Map1");
            mapString.put(8, "Map2");
            mapString.put(9, "Map3");

            if (i == 0) {
                mapNumber.put(4, null);
                mapString.put(7, null);
            }
            WriteModel model = WriteModel.builder().string("A").intNum(1000000000)
                    .longNum(1111111111111111111L)
                    .aaa("AAAA")
                    .sequence(i)
                    .enumType(EnumType.A)
                    .localDate(LocalDate.now()).mapString(mapString).mapNumber(mapNumber).build();
            result.add(model);
        }
        return result;
    }

}