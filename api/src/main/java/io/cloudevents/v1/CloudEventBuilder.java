package io.cloudevents.v1;

import io.cloudevents.Attributes;
import io.cloudevents.CloudEvent;
import io.cloudevents.impl.BaseCloudEventBuilder;

import java.net.URI;
import java.time.ZonedDateTime;

/**
 *
 * @author fabiojose
 * @author slinkydeveloper
 * @version 1.0
 */
public final class CloudEventBuilder extends BaseCloudEventBuilder<CloudEventBuilder, AttributesImpl> {

	private String id;
	private URI source;
	private String type;
	private String datacontenttype;
	private URI dataschema;
	private String subject;
	private ZonedDateTime time;

    public CloudEventBuilder() {
        super();
    }

    public CloudEventBuilder(CloudEvent event) {
        super(event);
    }

    @Override
    protected void setAttributes(Attributes attributes) {
        AttributesImpl attr = (AttributesImpl) attributes.toV1();
        this
            .withId(attr.getId())
            .withSource(attr.getSource())
            .withType(attr.getType());
        attr.getDataContentType().ifPresent(this::withDataContentType);
        attr.getDataSchema().ifPresent(this::withDataSchema);
        attr.getSubject().ifPresent(this::withSubject);
        attr.getTime().ifPresent(this::withTime);
    }

    public CloudEventBuilder withId(String id) {
		this.id = id;
		return this;
	}

	public CloudEventBuilder withSource(URI source) {
		this.source = source;
		return this;
	}

	public CloudEventBuilder withType(String type) {
		this.type = type;
		return this;
	}

	public CloudEventBuilder withDataSchema(URI dataschema) {
		this.dataschema = dataschema;
		return this;
	}

	public CloudEventBuilder withDataContentType(
			String datacontenttype) {
		this.datacontenttype = datacontenttype;
		return this;
	}

    public CloudEventBuilder withSubject(
			String subject) {
		this.subject = subject;
		return this;
	}

	public CloudEventBuilder withTime(ZonedDateTime time) {
		this.time = time;
		return this;
    }

    protected AttributesImpl buildAttributes() {
        return new AttributesImpl(id, source, type, datacontenttype, dataschema, subject, time);
    }
}
