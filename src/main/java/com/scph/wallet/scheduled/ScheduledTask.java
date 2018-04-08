package com.scph.wallet.scheduled;

import com.scph.wallet.client.Web3JClient;
import com.scph.wallet.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

@Component
public class ScheduledTask {
    Logger logger = LoggerFactory.getLogger(getClass());

    public final static long ONE_Second = 1000;
    public final static String Filename = "check-point.txt";

    @Scheduled(fixedRate = ONE_Second * 10)
    public void checkTransScheduled() {
        long checkBlockPoint = Long.parseLong(FileUtil.read(Filename));
        Admin web3j = Web3JClient.getAdminClient();
        try {
            PersonalListAccounts personalListAccounts = web3j.personalListAccounts().send();
            List<String> accountsList = personalListAccounts.getAccountIds();
            long currentAdvancedNum = web3j.ethBlockNumber().send().getBlockNumber().longValue();
            for (long i = checkBlockPoint; i <= currentAdvancedNum; i++) {
                if (i == currentAdvancedNum) {
                    FileUtil.write(Filename, currentAdvancedNum + "");
                    logger.info("区块扫描结束，最后扫描块为：" + currentAdvancedNum);
                }
                EthBlock ethBlock = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(i)), false).send();
                EthBlock.Block block = ethBlock.getResult();
                List<EthBlock.TransactionResult> tranList = ethBlock.getBlock().getTransactions();
                if (tranList.size() == 0)
                    continue;
                logger.info("当前块中交易数量：" + tranList.size());
                for (EthBlock.TransactionResult tran : tranList) {
                    String transactionHash = (String) tran.get();
                    EthTransaction ethTransaction = web3j.ethGetTransactionByHash(transactionHash).send();
                    Transaction transaction = ethTransaction.getTransaction().get();
                    String toAddress = transaction.getTo();
                    if (accountsList.contains(toAddress)) {
                        FileUtil.write(Filename, i + "");
                        // TODO: return transaction json data structure.
                        logger.info("监测到账户：" + toAddress + "发生交易。");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
