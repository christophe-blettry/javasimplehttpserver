/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author christophe
 */
public class BCryptUtil {

	private BCryptUtil() {
	}

	public static final String hash(String toHash) {
		return BCrypt.hashpw(toHash, BCrypt.gensalt());
	}

	public static final boolean check(String candidate, String hashed) {
		return BCrypt.checkpw(candidate, hashed);
	}

}
