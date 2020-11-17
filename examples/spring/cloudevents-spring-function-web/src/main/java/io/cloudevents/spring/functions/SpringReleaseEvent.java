/*
 * Copyright 2020-Present The CloudEvents Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cloudevents.spring.functions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * An example POJO that represents cloud event data
 *
 * @author Oleg Zhurakousky
 *
 */
public class SpringReleaseEvent {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "Europe/Paris")
	private Date releaseDate;

	private String releaseName;

	private String version;
	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public SpringReleaseEvent setReleaseDateAsString(String releaseDate) {
		try {
			this.releaseDate = new SimpleDateFormat("dd-MM-yyyy").parse(releaseDate);
		}
		catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public String getReleaseName() {
		return releaseName;
	}

	public SpringReleaseEvent setReleaseName(String releaseName) {
		this.releaseName = releaseName;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public SpringReleaseEvent setVersion(String version) {
		this.version = version;
		return this;
	}

	@Override
	public String toString() {
		return "releaseDate:" + new SimpleDateFormat("dd-MM-yyyy").format(releaseDate) + "; releaseName:" + releaseName + "; version:" + version;
	}
}
