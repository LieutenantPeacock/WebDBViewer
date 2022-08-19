/**
 *  Copyright (C) 2022  LieutenantPeacock
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package com.ltpeacock.dbviewer.util;

import static com.ltpeacock.dbviewer.commons.LogMarkers.BUILD_INFO;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

/**
 * @author LieutenantPeacock
 */
@Component
public class BuildInfo {
	private static final Logger LOG = LoggerFactory.getLogger(BuildInfo.class);
	/** Constant for {@code "git.branch"}. */
	public static final String GIT_BRANCH = "git.branch";
	/** Constant for {@code "git.build.time"}. */
	public static final String GIT_BUILD_TIME = "git.build.time";
	/** Constant for {@code "git.commit.id.abbrev"}. */
	public static final String GIT_COMMIT_ID_ABBREV = "git.commit.id.abbrev";
	/** Constant for {@code "git.tags"}. */
	public static final String GIT_TAGS = "git.tags";
	/** Constant for {@code "git.build.version"}. */
	public static final String GIT_BUILD_VERSION = "git.build.version";
	private Properties gitProps;

	@PostConstruct
	private void loadGitProps() {
		try {
			this.gitProps = PropertiesLoaderUtils.loadProperties(new ClassPathResource("git.properties"));
			if (LOG.isInfoEnabled())
				for(Map.Entry<?, ?> entry: new TreeMap<>(this.gitProps).entrySet())
					LOG.info(BUILD_INFO, "[{}] = [{}]",
							String.format("%25s", entry.getKey()), Util.toOneLine(entry.getValue()));
		} catch (IOException e) {
			LOG.debug("IOException loading git properties", e);
		}
	}

	public String get(final String key) {
		return this.gitProps.getProperty(key);
	}
}
