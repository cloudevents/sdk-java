/**
 * Copyright 2019 The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.cloudevents.extensions;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import org.junit.Test;

public class DatarefExtensionTest {

  @Test
  public void should_transport_format_ok() {
    // setup
    DatarefExtension datarefExtension = new DatarefExtension();
    datarefExtension.setDataref(URI.create("/dataref"));

    // act
    ExtensionFormat format = new DatarefExtension.Format(datarefExtension);

    // assert
    assertEquals("/dataref", format.transport().get("dataref"));
  }

  @Test
  public void should_inmemory_format_ok() {
    // setup
    DatarefExtension datarefExtension = new DatarefExtension();
    datarefExtension.setDataref(URI.create("/dataref"));

    // act
    ExtensionFormat format = new DatarefExtension.Format(datarefExtension);

    // assert
    assertEquals("dataref", format.memory().getKey());
    assertEquals(DatarefExtension.class, format.memory().getValueType());

    assertEquals("/dataref",
        ((DatarefExtension) format.memory().getValue()).getDataref().toString());
  }
}
