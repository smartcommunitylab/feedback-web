/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.trentorise.smartcampus.feedback.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.feedback.model.Feedback;
import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.storage.sync.mongo.BasicObjectSyncMongoStorage;

/**
 * Persistence manager for the feedback objects.
 * @author raman
 *
 */
@Component
public class FeedbackManager {

	private static final Log logger = LogFactory.getLog(FeedbackManager.class);

	@Autowired
	private BasicObjectSyncMongoStorage storage;

	public Feedback storeFeedback(Feedback f) throws DataException {
		logger.debug("Storing feedback: "+f);
		f.setReportTime(System.currentTimeMillis());
		storage.storeObject(f);
		return f;
	}
	
	/**
	 * @return all the feedback records 
	 * @throws DataException
	 */
	public List<Feedback> getAllFeedback() throws DataException {
		return storage.getObjectsByType(Feedback.class);
	}
	/**
	 * 
	 * @param app
	 * @return all the feedback of the specified app
	 * @throws DataException
	 */
	public List<Feedback> getFeedbackByApp(String app) throws DataException {
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("appId", app);
		return storage.searchObjects(Feedback.class, criteriaMap );
	}
	/**
	 * 
	 * @param app
	 * @return all the feedback of the specified app
	 * @throws DataException
	 */
	public List<Feedback> getFeedbackByActivity(String app, String activity) throws DataException {
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("appId", app);
		criteriaMap.put("acId", app);
		return storage.searchObjects(Feedback.class, criteriaMap );
	}

	/**
	 * @param creatorId
	 * @return all the feedback reported by the specified user
	 * @throws DataException
	 */
	public List<Feedback> getFeedbackByCreator(String creatorId) throws DataException {
		Map<String, Object> criteriaMap = new HashMap<String, Object>();
		criteriaMap.put("creatorId", creatorId);
		return storage.searchObjects(Feedback.class, criteriaMap );
	}
}
