package com.github.fnwib.write;

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
import java.util.List;
@Deprecated
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

            List<String> mapNumber = new ArrayList<>();
            mapNumber.add("null");
            mapNumber.add("Map2");
            mapNumber.add("Map3");
            List<String> mapString = new ArrayList<>();
            mapString.add("Map1");
            mapString.add("null");
            mapString.add("Map3");
            mapString.add("Map4");

            WriteModel model = new WriteModel();
            model.setString("A");
            model.setIntNum(1000000000);
            model.setLongNum(1111111111111111111L);
            model.setAaa("AAAA");
            model.setSequence(i);
            model.setEnumType(EnumType.A);
            model.setLocalDate(LocalDate.now());
            model.setLocalDate(LocalDate.now());
            model.setListString(mapString);
            model.setListNumber(mapNumber);
            result.add(model);
        }
        return result;
    }

}