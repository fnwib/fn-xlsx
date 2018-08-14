package com.github.fnwib.write;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.mapper.RowMapper;
import com.github.fnwib.mapper.RowMapperImpl;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.HeaderCreator;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.read.ExcelReader;
import com.github.fnwib.read.ExcelReaderImpl;
import com.github.fnwib.testentity.TestEnumType;
import com.github.fnwib.util.UUIDUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelWriterImplTest extends CommonPathTest {

	private List<Header> getHeader() {
		List<String> values = Lists.newArrayList();
		values.add("序号");
		values.add("字符串");
		values.add("数字");
		values.add("日期");
		values.add("集合 1");
		values.add("集合 2");
		values.add("集合 4");
		values.add("集合2 1");
		values.add("集合2 2");
		values.add("集合2 4");
		values.add("MAP A null");
		values.add("MAP B null");
		values.add("MAP C null");
		values.add("枚举");
		values.add("Nested A");
		values.add("Nested B");
		values.add("Nested C");
		values.add("Nested D");
		return HeaderCreator.create(new AtomicInteger(), values);
	}

	private List<TestModel> getDataList(int length) {
		List<TestModel> result = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			List<String> list1 = new ArrayList<>();
			list1.add(null);
			list1.add("Map2");
			list1.add("Map3");
			List<String> list2 = new ArrayList<>();
			list2.add("Map1");
			list2.add(null);
			list2.add("Map3");

			TestModel model = new TestModel();
			model.setSequence(i);
			model.setString(UUIDUtils.getHalfId());
			model.setIntNum(i << 5);
			model.setLocalDate(LocalDate.now());
			model.setList(list1);
			model.setList2(list2);
			model.setMapNull(Maps.newHashMap());
			model.setNoMatchMap(Maps.newHashMap());
			model.setEnumType(TestEnumType.A);
			TestNested2 nested2 = new TestNested2("aa" + i, "bb" + i);
			TestNested nested = new TestNested("aa" + i, "bb" + i, nested2);
			model.setTestNested(nested);
			result.add(model);
		}
		return result;
	}

	@Test
	public void write() {
		SheetConfig config = SheetConfig.builder()
				.fileName("test")
				.dir(basePath)
				.maxRowNumCanWrite(5)
				.addPreHeader(0, 0, "标题")
				.addHeaders(getHeader())
				.addHeaders("append after")
				.build();
		LocalConfig localConfig = new LocalConfig();
		localConfig.setMaxNestLevel(3);
		RowMapper<TestModel> mapper = new RowMapperImpl<>(TestModel.class, localConfig);
		ExcelWriter<TestModel> writer = new ExcelWriterImpl<>(config, mapper);
		List<TestModel> source = getDataList(6);
		writer.write(source);
		List<File> files = writer.getFiles();
		List<TestModel> target = read(files, mapper);
		Assert.assertSame("集合长度不一致", source.size(), target.size());
		for (int i = 0; i < source.size(); i++) {
			TestModel sourceModel = source.get(i);
			TestModel targetModel = target.get(i);
			sourceModel.setLineNum(targetModel.getLineNum());
			Assert.assertEquals("Equals", sourceModel, targetModel);
		}

	}

	private List<TestModel> read(List<File> files, RowMapper<TestModel> mapper) {
		List<TestModel> target = Lists.newArrayList();
		for (File file2 : files) {
			Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file2);
			ExcelReader<TestModel> reader = new ExcelReaderImpl<>(mapper, workbook, 0);
			List<TestModel> data = reader.fetchAllData();
			target.addAll(data);
			String preTitle = reader.getPreTitle(0, 0);
			Assert.assertEquals("0,0 标题不一致", "标题", preTitle);
		}
		target.sort(Comparator.comparing(TestModel::getSequence));
		return target;
	}

	@Test
	public void writeMergedRegion() {
		SheetConfig config = SheetConfig.builder()
				.fileName("test")
				.dir(basePath)
				.maxRowNumCanWrite(5)
				.addPreHeader(0, 0, "标题")
				.addHeaders(getHeader())
				.addHeaders("append after")
				.build();
		LocalConfig localConfig = new LocalConfig();
		localConfig.setMaxNestLevel(3);
		RowMapper<TestModel> mapper = new RowMapperImpl<>(TestModel.class, localConfig);
		ExcelWriter<TestModel> writer = new ExcelWriterImpl<>(config, mapper);

		List<TestModel> source = getDataList(6);

		writer.writeMergedRegion(source.subList(0, 1), Arrays.asList(0, 1, 2));
		writer.writeMergedRegion(source.subList(1, 3), Arrays.asList(0, 1, 2));
		writer.writeMergedRegion(source.subList(3, source.size()), Arrays.asList(0, 1, 2));

		List<File> files = writer.getFiles();
		List<TestModel> target = read(files, mapper);
		Assert.assertSame("集合长度不一致", source.size(), target.size());
		for (int i = 0; i < source.size(); i++) {
			TestModel sourceModel = source.get(i);
			TestModel targetModel = target.get(i);
			sourceModel.setLineNum(targetModel.getLineNum());
			Assert.assertTrue("Equals", Objects.deepEquals(sourceModel, targetModel));
		}

	}

	@ToString
	@EqualsAndHashCode
	@Getter
	@Setter
	public static class TestModel {
		@AutoMapping(operation = Operation.LINE_NUM)
		private Integer lineNum;
		@AutoMapping("序号")
		private Integer sequence;
		@AutoMapping("字符串")
		private String string;
		@AutoMapping("数字")
		private Integer intNum;
		@AutoMapping("日期")
		private LocalDate localDate;
		@AutoMapping(prefix = "集合", value = "\\d+")
		private List<String> list;
		@AutoMapping(prefix = "集合2", value = "\\d+")
		private List<String> list2;
		@AutoMapping("MAP [A-Z] null")
		private Map<Integer, String> mapNull;
		@AutoMapping("Excel no match")
		private Map<Integer, String> noMatchMap;
		@AutoMapping("枚举")
		private TestEnumType enumType;
		@AutoMapping(complex = ComplexEnum.Nested, order = 1)
		private TestNested testNested;
	}

	@ToString
	@Getter
	@Setter
	@EqualsAndHashCode
	@AllArgsConstructor
	@NoArgsConstructor
	public  static  class TestNested {
		@AutoMapping("Nested B")
		private String bb;
		@AutoMapping("Nested A")
		private String aa;
		@AutoMapping(complex = ComplexEnum.Nested, order = 1)
		private TestNested2 testNested2;
	}

	@ToString
	@Getter
	@Setter
	@EqualsAndHashCode
	@AllArgsConstructor
	@NoArgsConstructor
	public  static class TestNested2 {
		@AutoMapping("Nested C")
		private String bb;
		@AutoMapping("Nested D")
		private String aa;
	}

}