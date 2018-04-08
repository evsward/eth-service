package com.scph.wallet.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;

@Component
@ConfigurationProperties
public class Web3JClient {
    private static String rpcAddress;
    private volatile static Web3j web3j = null;
    private volatile static Admin web3jAdmin = null;

    public static Web3j getClient() {
        if (web3j == null) {
            synchronized (Web3JClient.class) {
                if (web3j == null) {
                    web3j = Web3j.build(new HttpService(rpcAddress));
                }
            }
        }
        return web3j;
    }

    public static Admin getAdminClient() {
        if (web3jAdmin == null) {
            synchronized (Web3JClient.class) {
                if (web3jAdmin == null) {
                    web3jAdmin = Admin.build(new HttpService(rpcAddress));
                }
            }
        }
        return web3jAdmin;
    }

    /**
     * 解决Springboot无法在静态变量中注入配置文件数据的问题
     *
     * @param rpcAddress
     */
    @Value("${ethereum.rpcAddress}")
    public void setRpcAddress(String rpcAddress) {
        Web3JClient.rpcAddress = rpcAddress;
    }
}
