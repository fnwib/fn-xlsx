package com.github.fnwib.annotation;

public enum BindType {
	//独占  被绑定后不允许被别的对象绑定
	Exclusive,
	//共享  被绑定后允许被别的对象绑定
	Nonexclusive;
}
