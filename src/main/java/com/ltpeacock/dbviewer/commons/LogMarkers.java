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
package com.ltpeacock.dbviewer.commons;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * @author LieutenantPeacock
 */
public final class LogMarkers {
	public static final Marker AUDIT = MarkerFactory.getMarker("AUDIT");
	public static final Marker FORBIDDEN_ACCESS = MarkerFactory.getMarker("FORB");
	public static final Marker BUILD_INFO = MarkerFactory.getMarker("BuildInfo");

	private LogMarkers() {
		throw new UnsupportedOperationException();
	}
}
