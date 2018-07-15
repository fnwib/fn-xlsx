package com.github.fnwib.databing;

import com.github.fnwib.databing.title.TitleResolver;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.google.common.collect.Lists;

import java.util.Collection;

public class LocalConfig {
	/**
	 * 配置最大嵌套层数
	 */
	private int maxNestLevel;

	private Collection<ValueHandler> readContentValueHandlers;
	private Collection<ValueHandler> titleValueHandlers;

	public LocalConfig() {
		this.readContentValueHandlers = Lists.newArrayList();
		this.titleValueHandlers = Lists.newArrayList();
		//默认支持两层
		this.maxNestLevel = 2;
	}

	public void setMaxNestLevel(int val) {
		this.maxNestLevel = Math.max(val, 1);
	}

	public int getMaxNestLevel() {
		return maxNestLevel;
	}

	private synchronized void registerReadContentValueHandler(ValueHandler valueHandler) {
		if (readContentValueHandlers.contains(valueHandler)) {
			return;
		}
		readContentValueHandlers.add(valueHandler);
	}

	public synchronized void registerReadContentValueHandlers(Collection<ValueHandler> valueHandlers) {
		for (ValueHandler valueHandler : valueHandlers) {
			registerReadContentValueHandler(valueHandler);
		}
	}

	public synchronized void registerReadContentValueHandlers(ValueHandler... valueHandlers) {
		for (ValueHandler valueHandler : valueHandlers) {
			registerReadContentValueHandler(valueHandler);
		}
	}

	public Collection<ValueHandler> getContentValueHandlers() {
		Collection<ValueHandler> valueHandlers = Lists.newArrayList();
		for (ValueHandler valueHandler : this.readContentValueHandlers) {
			valueHandlers.add(valueHandler);
		}
		return valueHandlers;
	}

	private synchronized void registerTitleValueHandler(ValueHandler valueHandler) {
		if (titleValueHandlers.contains(valueHandler)) {
			return;
		}
		titleValueHandlers.add(valueHandler);
	}


	public synchronized void registerTitleValueHandlers(Collection<ValueHandler> valueHandlers) {
		for (ValueHandler valueHandler : valueHandlers) {
			registerTitleValueHandler(valueHandler);
		}
	}

	public synchronized void registerTitleValueHandlers(ValueHandler... valueHandlers) {
		for (ValueHandler valueHandler : valueHandlers) {
			registerTitleValueHandler(valueHandler);
		}
	}

	public Collection<ValueHandler> getTitleValueHandlers() {
		Collection<ValueHandler> valueHandlers = Lists.newArrayList();
		for (ValueHandler valueHandler : this.titleValueHandlers) {
			valueHandlers.add(valueHandler);
		}
		return valueHandlers;
	}

	public TitleResolver getTitleResolver() {
		return new TitleResolver(readContentValueHandlers, titleValueHandlers);
	}

}
