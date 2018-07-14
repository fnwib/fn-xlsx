package com.github.fnwib.write.config;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.UUIDUtils;
import lombok.Getter;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * please user com.github.fnwib.write.model.SheetConfig
 */
@Deprecated
public class ResultFileSetting {

	private static final Logger log = LoggerFactory.getLogger(ResultFileSetting.class);


	private static final String EXTENSION = ".xlsx";

	private static final DecimalFormat TWO_DIGITS = new DecimalFormat("00");

	private final AtomicInteger fileSeq = new AtomicInteger(1);

	private final FileNameProducer exportFileName = (baseName, extension) -> baseName + "-" + TWO_DIGITS.format(fileSeq.getAndAdd(1)) + extension;

	private final File resultFolder;

	private final String baseName;

	//兼容
	@Getter
	private final String filename;
	//兼容
	@Getter
	private final String dir;

	/**
	 * @param filename     结果文件的名称
	 * @param resultFolder 结果文件存放的文件夹
	 */
	public ResultFileSetting(final String filename, final File resultFolder) {

		if (resultFolder == null) {
			throw new SettingException("存放结果的文件夹不能为空");
		}
		if (!resultFolder.exists()) {
			throw new SettingException("存放结果的文件夹" + resultFolder.getAbsolutePath() + "不存在");
		}
		if (resultFolder.isFile()) {
			throw new SettingException(resultFolder.getAbsolutePath() + "不是文件夹");
		}
		File[] listFiles = resultFolder.listFiles();
		if (listFiles == null) {
			throw new SettingException("文件无效");
		}
		if (listFiles.length > 0) {
			String uuid = UUIDUtils.getHalfId();
			this.resultFolder = new File(resultFolder.getAbsolutePath() + File.separator + uuid);
			log.info("[{}]不为空,将在该目录下创建[{}]目录并使用该目录", resultFolder.getAbsolutePath(), uuid);
		} else {
			this.resultFolder = resultFolder;
		}
		this.baseName = FilenameUtils.getBaseName(filename);
		this.filename = filename;
		this.dir = resultFolder.getAbsolutePath();

	}

	public File getNextResultFile() {
		String filename = exportFileName.getFilename(resultFolder.getAbsolutePath() + File.separator + baseName, EXTENSION);
		return new File(filename);
	}

	public File getResultFolder() {
		return resultFolder;
	}

	public File getEmptyFile() {
		Path path = Paths.get(resultFolder.getAbsolutePath(), UUIDUtils.getId() + EXTENSION);
		return path.toFile();
	}


}
