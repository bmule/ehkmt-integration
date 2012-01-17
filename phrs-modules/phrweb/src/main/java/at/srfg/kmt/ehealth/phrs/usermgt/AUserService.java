package at.srfg.kmt.ehealth.phrs.usermgt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import at.srfg.kmt.ehealth.phrs.model.baseform.AUser;

public class AUserService {
	private static final Map<String, AUser> TEST_USERS = new HashMap<String, AUser>();
	static {
		Map<String, String> attrs = new HashMap<String, String>();

		TEST_USERS.put("test1", new AUser("test1", "Admin",
				"xxx@gmail.com", attrs));
		TEST_USERS.put("test3", new AUser("test2", "test1",
				"xxx@gmail.com", attrs));
		/*
		 TEST_USERS.put("test4", new AUser("test3", "test2", "testicard2@gmail.com",attrs)); 
		 TEST_USERS.put("test5", new AUser("test4", "test3", "testicard3@gmail.com",attrs));
		 TEST_USERS.put("test6", new AUser("test5", "test4", "testicard4@gmail.com",attrs)); 
		 TEST_USERS.put("test7", new AUser("test6", "test5", "testicard5@gmail.com",attrs));
		 TEST_USERS.put("test8", new AUser("test7", "test6", "testicard6@gmail.com",attrs));
		 */

	}

	public String create(AUser user) {
		if (user == null) {
			throw new RuntimeException("AUser object is null.");
		}
		String ownerUri = UUID.randomUUID().toString();
		user.setOwnerUri(ownerUri);
		TEST_USERS.put(ownerUri, user);
		return ownerUri;
	}

	public void delete(AUser user) {
		if (user == null) {
			throw new RuntimeException("AUser object is null.");
		}
		TEST_USERS.remove(user.getOwnerUri());
	}

	public Collection<AUser> getAllUsers() {
		return TEST_USERS.values();
	}

	public AUser getUser(Integer userId) {
		return TEST_USERS.get(userId);
	}

	public Collection<AUser> searchUsers(String username) {
		String searchCriteria = (username == null) ? "" : username
				.toLowerCase().trim();
		Collection<AUser> users = TEST_USERS.values();
		Collection<AUser> searchResults = new ArrayList<AUser>();
		for (AUser user : users) {
			if (user.getUsername() != null
					&& user.getUsername().toLowerCase().trim()
							.startsWith(searchCriteria)) {
				searchResults.add(user);
			}
		}
		return searchResults;
	}

	public void update(AUser user) {
		if (user == null || !TEST_USERS.containsKey(user.getOwnerUri())) {
			throw new RuntimeException("AUser object null or User Id ["
					+ user.getOwnerUri() + "] invalid.");
		}
		TEST_USERS.put(user.getOwnerUri(), user);
	}

}