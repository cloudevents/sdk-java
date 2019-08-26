package io.cloudevents.v03;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The specification reserved words: the context attributes
 * 
 * @author fabiojose
 *
 */
public enum ContextAttributes {

	id,
	source,
	specversion,
	type,
	time,
	schemaurl,
	datacontenttype,
	datacontentencoding,
	subject;
	
	public static final List<String> VALUES = 
		Arrays.asList(ContextAttributes.values())
		.stream()
		.map(Enum::name)
		.collect(Collectors.toList());
}
