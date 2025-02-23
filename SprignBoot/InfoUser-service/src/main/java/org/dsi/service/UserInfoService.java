package org.dsi.service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import java.util.UUID;

import org.dsi.entity.InfoUser;
import org.dsi.mail.MailService;
import org.dsi.payload.UserInfo;
import org.dsi.payload.VerifyEmail;
import org.dsi.repo.NodeSync;
import org.dsi.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.bytebuddy.utility.RandomString;
import net.minidev.json.JSONObject;

@Service
public class UserInfoService {
	
	@Autowired
	UserRepo userRepo;
	
	@Autowired
	NodeSync nodeSync;

	
	@Autowired
	MailService mailService;
	
	private static final String ALLOWED_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int STRING_LENGTH = 6;
	
    public static String generateRandomString() {
        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(STRING_LENGTH);

        for (int i = 0; i < STRING_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }

	public InfoUser getInfoUserByEmail(String email) throws Exception {
	    InfoUser user = userRepo.getUserByemail(email);
	    if (user == null) {
	        throw new Exception("User not found for email: " + email);
	    }
	    return user;
	}
	
	public InfoUser getUserByIdKeyCloak(String id) throws Exception {
	    InfoUser user = userRepo.getUserByIdKeyCloak(id);
	    if (user == null) {
	        throw new Exception("User not found for KeyCloakId: " + id);
	    }
	    return user;
	}
	
	public InfoUser getInfoUserById(Long id) throws Exception {
	    InfoUser user = userRepo.getUserById(id);
	    if (user == null) {
	        throw new Exception("User not found for id: " + id);
	    }
	    return user;
	}
	
	public void addInfoUser(UserInfo user) throws Exception {
		
	    InfoUser newuser = new   InfoUser();
	    Map<String, Object> roleData = new HashMap<>();
	    roleData.put("role", user.getRole());

	    try {
	    	if(userRepo.getUserByemail(user.getEmail())==null) {
				newuser.setFirstName(user.getFirstName());
				newuser.setLastName(user.getLastName());
				if(user.getCin()!=null) {
					newuser.setCin(user.getCin());
				}
				newuser.setEmail(user.getEmail());
				newuser.setPassword(user.getPassword());
				newuser.setNumTlf(user.getNumTlf());
				newuser.setPhoto(null);
				newuser.setStatus(0);
				newuser.setSexe(user.getSexe());
				newuser.setPhotoCin(user.getPhotoCin());
				newuser.setCode(generateRandomString());
				newuser.setRole(user.getRole());
			    mailService.sendVerificationEmail(newuser);
				newuser.setKeycloak_id(user.getKeycloak_id());
				userRepo.save(newuser);
				JSONObject jsoUser=new JSONObject();
	  			jsoUser.appendField("Name",user.getFirstName());
	  			jsoUser.appendField("LastName",user.getLastName());
	  			jsoUser.appendField("email",user.getEmail());
	  			if(user.getCin()!=null) {
	  				jsoUser.appendField("cin",user.getCin());
				}
	  			jsoUser.appendField("status",0);
	  			jsoUser.appendField("_id",newuser.getId());
	  			jsoUser.appendField("Photo",null);
	  			jsoUser.appendField("roles",roleData);
	  			nodeSync.addInfoUser(jsoUser);
	    	}else {
	    		throw new Exception("User with email " + user.getEmail() + " already exists.");
	    	}
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
	

	public void updateUser(InfoUser user,Long id) throws Exception {
		
		InfoUser user1 = userRepo.getUserById(id);
		
		if(user1==null) {
			
			throw new Exception("User not found");
			
		}else {
			
			user1.setFirstName(user.getFirstName());
			user1.setLastName(user.getLastName());
			user1.setNumTlf(user.getNumTlf());
			user1.setCin(user.getCin());
			user1.setSexe(user.getSexe());
			userRepo.save(user1);
			
		}
		
	}
	
	public void verifyEmail(VerifyEmail data) throws Exception {
		InfoUser user = userRepo.verifyEmail(data.getEmail(),data.getCode());
		if(user==null) {
			throw new Exception("email or code invalid");
		}else {
			user.setEmail_verified_at(new Date());
			userRepo.save(user);
		}
	}
	
	
	 public void forgotPassword(String email)throws Exception {
		  InfoUser user=userRepo.getUserByemail(email);
		  System.out.println(user);
		  if(user==null) {
			  throw new Exception("Email Not Found");
		  }else {
			  String Token=RandomString.make(8);
			  user.setPassword_token(Token);
			  userRepo.save(user);
			  mailService.SendForgotPassword(user, Token);
		   }
	  }
	 
	 public void ChangerPassword(String email,String Token,String password)throws Exception {
		  InfoUser user=userRepo.getUserByemail(email);
		  if(user==null) {
			  throw new Exception("Email Not Found");
		  }else {
			  if(userRepo.CheckToken(Token)==null) {
				  throw new Exception("Token Not Found");
			  }else {
				  if(user.getPassword_token().equals(Token)) {
					  user.setPassword(password);
					  user.setPassword_token(null);
					  userRepo.save(user);
				  }else {
					  throw new Exception("Token doesn't match");
				  }
			  }
		   }
	  }

	

}