/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.sync.engine.documentlibrary.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.liferay.sync.engine.documentlibrary.event.Event;
import com.liferay.sync.engine.model.SyncFile;
import com.liferay.sync.engine.service.SyncFileService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Shinn Lok
 */
public class MoveFileEntryHandler extends BaseJSONHandler {

	public MoveFileEntryHandler(Event event) {
		super(event);
	}

	@Override
	public boolean handlePortalException(String exception) throws Exception {
		if (exception.equals(
				"com.liferay.portlet.documentlibrary.DuplicateFileException")) {

			if (_logger.isDebugEnabled()) {
				_logger.debug("Handling exception {}", exception);
			}

			SyncFile localSyncFile = getLocalSyncFile();

			localSyncFile.setState(SyncFile.STATE_SYNCED);
			localSyncFile.setUiEvent(SyncFile.UI_EVENT_UPLOADED);

			SyncFileService.update(localSyncFile);

			return true;
		}

		return super.handlePortalException(exception);
	}

	@Override
	public void processResponse(String response) throws Exception {
		ObjectMapper objectMapper = new ObjectMapper();

		SyncFile remoteSyncFile = objectMapper.readValue(
			response, new TypeReference<SyncFile>() {});

		SyncFile localSyncFile = getLocalSyncFile();

		localSyncFile.setModifiedTime(remoteSyncFile.getModifiedTime());
		localSyncFile.setState(SyncFile.STATE_SYNCED);
		localSyncFile.setUiEvent(SyncFile.UI_EVENT_UPLOADED);

		SyncFileService.update(localSyncFile);
	}

	private static final Logger _logger = LoggerFactory.getLogger(
		MoveFileEntryHandler.class);

}