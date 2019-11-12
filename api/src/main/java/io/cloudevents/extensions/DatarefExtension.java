package io.cloudevents.extensions;

import java.net.URI;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DatarefExtension {

  private URI dataref;

  public URI getDataref() {
    return dataref;
  }

  public void setDataref(URI dataref) {
    this.dataref = dataref;
  }

  @Override
  public String toString() {
    return "DatarefExtension{" +
        "dataref='" + dataref + '\'' +
        '}';
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((dataref == null) ? 0
        : dataref.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    DatarefExtension other = (DatarefExtension) obj;
    if (dataref == null) {
      return other.dataref == null;
    } else {
      return dataref.equals(other.dataref);
    }
  }

  /**
   * The in-memory format for dataref.
   */
  public static class Format implements ExtensionFormat {

    public static final String IN_MEMORY_KEY = "dataref";
    public static final String DATAREF_KEY = "dataref";

    private final InMemoryFormat memory;
    private final Map<String, String> transport = new HashMap<>();

    public Format(DatarefExtension extension) {
      Objects.requireNonNull(extension);

      memory = InMemoryFormat.of(IN_MEMORY_KEY, extension,
          DatarefExtension.class);

      transport.put(DATAREF_KEY, extension.getDataref().toString());
    }

    @Override
    public InMemoryFormat memory() {
      return memory;
    }

    @Override
    public Map<String, String> transport() {
      return transport;
    }
  }

  /**
   * Unmarshals the {@link DatarefExtension} based on map of extensions.
   */
  public static Optional<ExtensionFormat> unmarshall(Map<String, String> exts) {
    String dataref = exts.get(Format.DATAREF_KEY);

    if (null != dataref) {
      DatarefExtension datarefExtension = new DatarefExtension();
      datarefExtension.setDataref(URI.create(dataref));

      InMemoryFormat inMemory = InMemoryFormat
          .of(Format.IN_MEMORY_KEY, datarefExtension, DatarefExtension.class);

      return Optional.of(
          ExtensionFormat.of(inMemory,
              new SimpleEntry<>(Format.DATAREF_KEY, dataref))
      );

    }

    return Optional.empty();
  }
}
