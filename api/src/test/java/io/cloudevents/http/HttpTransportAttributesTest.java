/**
 * Copyright 2018 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cloudevents.http;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.cloudevents.fun.BinaryFormatAttributeMapper;

public class HttpTransportAttributesTest {

    @Test
    public void testVersion02Headers() {
    	// setup
    	Map<String, Object> myHeaders = new HashMap<>();
    	myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "0.2");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-schemaurl", "http://my.br");
		myHeaders.put("Content-Type", "application/json");

		// act
		BinaryFormatAttributeMapper mapper = new io.cloudevents.v02.http.BinaryFormatAttributeMapperImpl();
		Map<String, String> attributes = mapper.map(myHeaders);

        // assert 
        assertEquals("0x11", attributes.get("id"));
        assertEquals("/source", attributes.get("source"));
        assertEquals("0.2", attributes.get("specversion"));
        assertEquals("br.my", attributes.get("type"));
        assertEquals("2019-09-16T20:49:00Z", attributes.get("time"));
        assertEquals("http://my.br", attributes.get("schemaurl"));
        assertEquals("application/json", attributes.get("contenttype"));
    }
    
    @Test
    public void should_map_headers_v03() {
    	// setup
    	Map<String, Object> myHeaders = new HashMap<>();
    	myHeaders.put("ce-id", "0x11");
		myHeaders.put("ce-source", "/source");
		myHeaders.put("ce-specversion", "0.2");
		myHeaders.put("ce-type", "br.my");
		myHeaders.put("ce-time", "2019-09-16T20:49:00Z");
		myHeaders.put("ce-schemaurl", "http://my.br");
		myHeaders.put("Content-Type", "application/json");
		myHeaders.put("ce-datacontentencoding", "base64");
		myHeaders.put("ce-subject", "the subject");

		// act
		BinaryFormatAttributeMapper mapper = new io.cloudevents.v03.http.BinaryFormatAttributeMapperImpl();
		Map<String, String> attributes = mapper.map(myHeaders);

        // assert 
        assertEquals("0x11", attributes.get("id"));
        assertEquals("/source", attributes.get("source"));
        assertEquals("0.2", attributes.get("specversion"));
        assertEquals("br.my", attributes.get("type"));
        assertEquals("2019-09-16T20:49:00Z", attributes.get("time"));
        assertEquals("http://my.br", attributes.get("schemaurl"));
        assertEquals("application/json", attributes.get("datacontenttype"));
        assertEquals("base64", attributes.get("datacontentencoding"));
        assertEquals("the subject", attributes.get("subject"));
    }
}
