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

package eu.trentorise.smartcampus.feedback.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eu.trentorise.smartcampus.ac.provider.AcService;
import eu.trentorise.smartcampus.ac.provider.AcServiceException;
import eu.trentorise.smartcampus.ac.provider.filters.AcProviderFilter;
import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.feedback.manager.FeedbackFileManager;
import eu.trentorise.smartcampus.feedback.manager.FeedbackManager;
import eu.trentorise.smartcampus.feedback.model.Feedback;

/**
 * Feedback controller
 * @author raman
 *
 */
@Controller
public class FeedbackController {

	private static final Log logger = LogFactory.getLog(FeedbackController.class);
	
	@Autowired
	private AcService acService;

	@Autowired
	private FeedbackManager feedbackManager;
	@Autowired
	private FeedbackFileManager feedbackFileManager;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	/**
	 * Upload feedback data. Take as input {@link Feedback} instance as JSON String passed 
	 * in 'body' param and the {@link MultipartFile} file param for the (optional) screenshot data.
	 * @param request
	 * @param response
	 * @param body
	 * @param file
	 * @return
	 */
	@RequestMapping(value = "/feedback", method = RequestMethod.POST)
	public @ResponseBody
	String feedback(HttpServletRequest request, 
			HttpServletResponse response, 
			@RequestParam("body") String body,
			@RequestParam(required=false,value="file") MultipartFile file) 
	{
		User user = null;
		try {
			user = retrieveUser(request, response);
		} catch (AcServiceException e) {
			logger.error("Error reading the user: "+e.getMessage());
		}
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		
		try {
			Feedback feedback = mapper.readValue(body, Feedback.class);
			if (feedback == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return null;
			}
			feedback.setCreatorId(""+user.getId());
			feedback.setUser(feedback.getCreatorId());
			
			if (file != null) {
				String fileId = feedbackFileManager.storeFile(file.getBytes());
				feedback.setFileId(fileId);
			}
			feedback = feedbackManager.storeFeedback(feedback);
			return feedback.getId();
		} catch (Exception e) {
			logger.error("Error storing feedback: "+e.getMessage());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
	}	
	

	private User retrieveUser(HttpServletRequest request, HttpServletResponse response) throws AcServiceException  {
		String token = request.getHeader(AcProviderFilter.TOKEN_HEADER);
		return acService.getUserByToken(token);
	}


}
