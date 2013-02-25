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

package eu.trentorise.smartcampus.feedback.test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.feedback.manager.FeedbackFileManager;
import eu.trentorise.smartcampus.feedback.manager.FeedbackManager;
import eu.trentorise.smartcampus.feedback.model.Feedback;
import eu.trentorise.smartcampus.feedback.model.FeedbackFile;
import eu.trentorise.smartcampus.presentation.common.exception.DataException;
import eu.trentorise.smartcampus.presentation.storage.sync.mongo.SyncObjectBean;

/**
 * @author raman
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class TestFeedbackManagers {

	@Autowired
	private FeedbackFileManager feedbackFileManager;
	@Autowired
	private FeedbackManager feedbackManager;
	@Autowired
	private MongoTemplate template;

	@Test
	public void testFeedback() throws DataException, IOException {
		template.dropCollection(SyncObjectBean.class);
		template.dropCollection(FeedbackFile.class);
		
		byte[] data = readTestFile(); 
		
		Feedback feedback = new Feedback();
		feedback.setActivityId("activity");
		feedback.setAppId("app");
		feedback.setDifficulty(3);
		feedback.setCreatorId("1");
		feedback.setNote("Note");
		
		String fileId = feedbackFileManager.storeFile(data);
		assert fileId != null;
		
		feedback.setFileId(fileId);
		feedback = feedbackManager.storeFeedback(feedback);
		assert feedback.getId() != null;
		
		List<Feedback> list = feedbackManager.getAllFeedback();
		assert list != null && list.size() == 1;
		String newFileId = list.get(0).getFileId();
		byte[] newData = feedbackFileManager.readFile(newFileId);
		assert newData != null && newData.length == data.length;
		writeTestFile(newData);
	}

	private byte[] readTestFile() throws IOException {
		RandomAccessFile f = new RandomAccessFile("src/test/resources/android.jpg", "r");
		byte[] b = new byte[(int)f.length()];
		f.read(b);
		f.close();
		return b;
	}
	private void writeTestFile(byte[] data) throws IOException {
		RandomAccessFile f = new RandomAccessFile("src/test/resources/android_copy.jpg", "rw");
		f.write(data);
		f.close();
	}
}
