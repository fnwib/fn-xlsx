package com.github.fnwib.testentity;

import com.github.fnwib.annotation.AutoMapping;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TestNested {
	@AutoMapping("Nested B")
	private String bb;
	@AutoMapping("Nested A")
	private String aa;
}