package com.github.fnwib.write.config;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.UUIDUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Slf4j
public class ResultFileSetting {

    private static final String EXTENSION = ".xlsx";

    private static final int STEP = 500000;

    private static final DecimalFormat TWO_DIGITS = new DecimalFormat("00");

    private final AtomicInteger fileSeq = new AtomicInteger(1);

    private final FileNameProducer exportFileName = (baseName, extension) -> baseName + "-" + TWO_DIGITS.format(fileSeq.getAndAdd(1)) + extension;

    private final File resultFolder;

    private final int maxRowsCanWrite;

    private final String baseName;


    /**
     * @param maxRowCanWrite sheet可写入最大行
     * @param filename       结果文件的名称
     * @param resultFolder   结果文件存放的文件夹
     */
    public ResultFileSetting(int maxRowCanWrite, final String filename, final File resultFolder) {
        if (maxRowCanWrite <= 0) {
            throw new SettingException("Sheet可写入最大行不能小于等于0");
        }
        this.maxRowsCanWrite = maxRowCanWrite;
        if (resultFolder == null) {
            throw new SettingException("存放结果的文件夹不能为空");
        }
        if (!resultFolder.exists()) {
            throw new SettingException("存放结果的文件夹" + resultFolder.getAbsolutePath() + "不存在");
        }
        if (resultFolder.isFile()) {
            throw new SettingException(resultFolder.getAbsolutePath() + "不是文件夹");
        }
        if (resultFolder.listFiles().length > 0) {
            String uuid = UUIDUtils.getHalfId();
            this.resultFolder = new File(resultFolder.getAbsolutePath() + File.separator + uuid);
            log.info("[{}]不为空,将在该目录下创建[{}]目录并使用该目录", resultFolder.getAbsolutePath(), uuid);
        } else {
            this.resultFolder = resultFolder;
        }
        this.baseName = FilenameUtils.getBaseName(filename);
//        this.extension = "." + FilenameUtils.getExtension(filename);

    }

    public ResultFileSetting(final String filename, final File resultFolder) {
        this(STEP, filename, resultFolder);
    }

    public File getNextResultFile() {
        String filename = exportFileName.getFilename(resultFolder.getAbsolutePath() + File.separator + baseName, EXTENSION);
        return new File(filename);
    }

    public File copyFile(File source) {
        String template = resultFolder.getAbsolutePath() + File.separator + UUIDUtils.getId() + EXTENSION;
        File target = new File(template);
        try {
            FileUtils.copyFile(source, target);
        } catch (IOException e) {
            log.error("复制文件错误 source[{}],target [{}]", source.getAbsolutePath(), target.getAbsolutePath());
            throw new SettingException("复制文件错误");
        }
        return target;
    }


    public boolean valid(AtomicInteger rowNum) {
        return rowNum.get() <= maxRowsCanWrite;
    }

}
