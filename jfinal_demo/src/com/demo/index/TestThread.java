package com.demo.index;

public class TestThread extends Thread {
	
	public void run(){
		System.out.println("线程："+this.getName()+"  线程号： "+this.getId());
		new IndexController().initFile();
	}
}
