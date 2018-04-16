package com.inschos.cloud.trading.access.rpc.service;

import hprose.client.HproseHttpClient;
import org.springframework.stereotype.Component;

@Component("phpTestClient")
public class PhpTestClient {
	private HproseHttpClient client = new HproseHttpClient("http://192.168.10.122:9600/api/rpc/test");

	public Test getPhpTest() {
		PhpTestService service = client.useService(PhpTestService.class);
		return service.getPhpTestA();
	}
}
