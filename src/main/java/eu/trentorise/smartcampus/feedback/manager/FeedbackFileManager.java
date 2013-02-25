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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.feedback.model.FeedbackFile;

/**
 * Manager for binary data files for feedback. 
 * @author raman
 *
 */
@Component
public class FeedbackFileManager {

	private static final Log logger = LogFactory.getLog(FeedbackFileManager.class);

	@Autowired
	private MongoTemplate template;

	/**
	 * Store binary data
	 * @param data
	 * @return id of the stored object
	 */
	public String storeFile(byte[] data) {
		logger.debug("Storing binary file");
		String id = ObjectId.get().toString();
		FeedbackFile file = new FeedbackFile();
		file.setId(id);
		file.setBinData(data);
		template.save(file);
		return id;
	}
	
	/**
	 * Read the stored binary data given the specified object Id
	 * @param id
	 * @return
	 */
	public byte[] readFile(String id) {
		FeedbackFile file = template.findById(id, FeedbackFile.class);
		return file.getBinData();
	}
}
