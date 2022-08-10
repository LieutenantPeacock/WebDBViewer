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
package com.ltpeacock.dbviewer.service;

import java.util.List;

import org.springframework.validation.BindingResult;

import com.colonelparrot.dbviewer.db.TableData;
import com.ltpeacock.dbviewer.db.QueryResult;
import com.ltpeacock.dbviewer.db.SortDirection;
import com.ltpeacock.dbviewer.db.dto.DBConnectionDefDTO;
import com.ltpeacock.dbviewer.form.ConnectionForm;
import com.ltpeacock.dbviewer.response.MappedErrorsResponse;
import com.ltpeacock.dbviewer.response.MappedMultiErrorsResponse;
import com.ltpeacock.dbviewer.response.SimpleResponse;

/**
 * @author LieutenantPeacock
 */
public interface DBConnectionService {
	MappedErrorsResponse<DBConnectionDefDTO> createConnection(final ConnectionForm form, 
			final BindingResult result, final long userId);
	List<String> getTables(final long connectionId, final long userId);
	SimpleResponse<TableData> getTableContents(final long connectionId, final long userId, final String tableName,
			final int page, final String sortColumn, final SortDirection dir, final String where);
	SimpleResponse<QueryResult> executeQuery(final long connectionId, final long userId, 
			final String query);
	DBConnectionDefDTO getConnectionDetails(final long id);
	MappedMultiErrorsResponse<DBConnectionDefDTO> updateConnection(final ConnectionForm form, 
			final BindingResult result);
}
