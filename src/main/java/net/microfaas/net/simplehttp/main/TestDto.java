/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.microfaas.net.simplehttp.main;

/**
 *
 * @author christophe
 */
public class TestDto {

	private int value;
	private String name;

	public TestDto() {
	}

	public TestDto(int value, String name) {
		this.value = value;
		this.name = name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "TestDto{" + "value=" + value + ", name=" + name + '}';
	}
	
}
