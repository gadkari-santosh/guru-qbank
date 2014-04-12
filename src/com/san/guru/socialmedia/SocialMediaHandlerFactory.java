package com.san.guru.socialmedia;

import static com.san.guru.constant.AppConstants.*;

public class SocialMediaHandlerFactory {

	private static final SocialMediaHandlerFactory INSTANCE = new SocialMediaHandlerFactory();
	
	private SocialMediaHandlerFactory() {}
	
	public static SocialMediaHandlerFactory getInstance() {
		return INSTANCE;
	}
	
	public ISocialMediaHandler getSocialMediaHandler(String name) {
		
		if (SOCIAL_MEDIA_FB.equalsIgnoreCase(name)) {
			return new FacebookHandler();
		} else if (SOCIAL_MEDIA_LN.equalsIgnoreCase(name)) {
			return new LinkedInHandler();
		}
		
		return null;
	}
}
