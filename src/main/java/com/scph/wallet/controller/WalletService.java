package com.scph.wallet.controller;

import com.scph.wallet.client.Web3JClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/eth")
public class WalletService {

    @RequestMapping(value = "/createAccount")
    @CrossOrigin
    public NewAccountIdentifier createAccount(@RequestParam(value = "password", required = true) String password) {
        NewAccountIdentifier newAccount = new NewAccountIdentifier();
        if ("".equals(password)) {
            newAccount.setResult("fail");
            newAccount.setError(new Response.Error(1, "password can't be null"));
            return newAccount;
        }
        Admin web3j = Web3JClient.getAdminClient();
        try {
            // geth要支持rpcapi包括 personal web3
            newAccount = web3j.personalNewAccount(password).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newAccount;
    }

    @RequestMapping(value = "/dealTransaction")
    @CrossOrigin
    public EthSendTransaction dealTransaction(@RequestParam(value = "from", required = true) String from,
                                              @RequestParam(value = "fromPassword", required = true) String fromPassword,
                                              @RequestParam(value = "to", required = true) String to,
                                              @RequestParam(value = "amount", required = true) long amount) {
        Admin web3j = Web3JClient.getAdminClient();
        PersonalUnlockAccount personalUnlockAccount = null;
        EthSendTransaction ethSendTransaction = null;
        try {
            personalUnlockAccount = web3j.personalUnlockAccount(from, fromPassword).sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (personalUnlockAccount != null && personalUnlockAccount.accountUnlocked()) {
            BigInteger amountWei = BigInteger.valueOf((long) (amount * Math.pow(10, 18)));
            Transaction transaction = new Transaction(from, null, null, null, to, amountWei, null);
            try {
                ethSendTransaction = web3j.personalSendTransaction(transaction, fromPassword).send();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ethSendTransaction;
    }

}
