/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.main;

import java.util.Arrays;
import java.util.Collections;
import net.microfaas.net.simplehttp.HttpServer;
import net.microfaas.net.simplehttp.SimpleHttpException;

/**
 *
 * @author christophe
 */
public class NewMain {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws SimpleHttpException  {
		HttpServer server = new HttpServer();
		server.setSearchClassForAnnotations(Collections.singleton("net.microfaas.net.simplehttp.example"));
		Thread t = new Thread(server);
		t.start();
	}

}
