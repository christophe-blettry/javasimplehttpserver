/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.main;

import java.util.Arrays;
import net.microfaas.net.simplehttp.HttpServer;
import net.microfaas.net.simplehttp.HttpSimpleException;

/**
 *
 * @author christophe
 */
public class NewMain {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws HttpSimpleException  {
		HttpServer server = new HttpServer();
		server.setSearchClassForAnnotations(Arrays.asList(new String[]{"net.microfaas.net.simplehttp.main"}));
		Thread t = new Thread(server);
		t.start();
	}

}
