package com.github.fnwib.write;

import com.github.fnwib.mapping.RowMappingImpl;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.model.ExcelHeader;
import com.github.fnwib.write.model.ExcelHeaderCreater;
import com.github.fnwib.write.model.SheetConfig;
import com.github.fnwib.testentity.TestModel;
import com.github.fnwib.testentity.TestNested;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import model.EnumType;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelWriterImplTest extends CommonPathTest {

	protected List<ExcelHeader> getHeader() {
		List<String> values = Lists.newArrayList();
		values.add("行号");
		values.add("序号");
		values.add("字符串");
		values.add("数字");
		values.add("日期");
		values.add("集合 1");
		values.add("集合 2");
		values.add("集合 4");
		values.add("MAP A null");
		values.add("MAP B null");
		values.add("MAP C null");
		values.add("枚举");
		values.add("Nested B");
		values.add("Nested A");
		return ExcelHeaderCreater.create(new AtomicInteger(), values);
	}

	List<TestModel> getDataList(int length) {
		List<TestModel> result = new ArrayList<>(length);
		for (int i = 0; i < length; i++) {
			List<String> list1 = new ArrayList<>();
			list1.add("null");
			list1.add("Map2");
			list1.add("Map3");
			List<String> list2 = new ArrayList<>();
			list2.add("Map1");
			list2.add("null");
			list2.add("Map3");

			TestModel model = new TestModel();
			model.setSequence(i);
			model.setString(UUIDUtils.getHalfId());
			model.setIntNum(i << 5);
			model.setLocalDate(LocalDate.now());
			model.setList(list1);
			model.setList(list2);
			model.setMapNull(Maps.newHashMap());
			model.setNoMatchMap(Maps.newHashMap());
			model.setEnumType(EnumType.A);
			model.setTestNested(new TestNested("aa" + i, "bb" + i));
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
		RowMappingImpl<TestModel> rowMapping = new RowMappingImpl<>(TestModel.class);
		List<TestModel> target = writeAndRead(config, rowMapping);
		List<TestModel> source = getDataList(6);

//		Assert.assertSame("集合长度不一致", source.size(), target.size());
//		for (int i = 0; i < source.size(); i++) {
//			TestModel sourceModel = source.get(i);
//			TestModel targetModel = target.get(i);
//			Assert.assertEquals("字符串不一致", StringUtils.trimToEmpty(sourceModel.getString()), StringUtils.trimToEmpty(targetModel.getString()));
//			Assert.assertEquals("数字int不一致", sourceModel.getIntNum(), targetModel.getIntNum());
//			Assert.assertEquals("数字long不一致", sourceModel.getLongNum(), targetModel.getLongNum());
//			Assert.assertEquals("日期不一致", sourceModel.getLocalDate(), targetModel.getLocalDate());
//
//			List<String> sourceNumberList = sourceModel.getListNumber();
//			List<String> targetNumberList = targetModel.getListNumber();
//			Assert.assertArrayEquals("List number 值不一致", sourceNumberList.toArray(), targetNumberList.toArray());
//
//			List<String> sourceStringMap = sourceModel.getListNumber();
//			List<String> targetStringMap = targetModel.getListNumber();
//			Assert.assertArrayEquals("List String 值不一致", sourceStringMap.toArray(), targetStringMap.toArray());
//			Assert.assertEquals("枚举", sourceModel.getEnumType(), targetModel.getEnumType());
//		}

	}

	private List<TestModel> writeAndRead(SheetConfig config, RowMappingImpl<TestModel> rowMapping) {
		List<TestModel> source = getDataList(6);
		List<TestModel> target = new ArrayList<>();
		ExcelWriter<TestModel> writer = new ExcelWriterImpl<>(config, rowMapping);
		writer.write(source);
		List<File> files = writer.getFiles();
		Collections.sort(target, Comparator.comparing(TestModel::getSequence));
		return target;
	}

	@Test
	public void write1() {
	}

	@Test
	public void writeMergedRegion() {
	}

	@Test
	public void flush() {
	}

	@Test
	public void getFiles() {
	}
}