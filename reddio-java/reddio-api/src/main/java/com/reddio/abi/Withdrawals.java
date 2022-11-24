package com.reddio.abi;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.2.
 */
@SuppressWarnings("rawtypes")
public class Withdrawals extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_CALCULATEASSETIDWITHTOKENID = "calculateAssetIdWithTokenId";

    public static final String FUNC_CALCULATEMINTABLEASSETID = "calculateMintableAssetId";

    public static final String FUNC_GETASSETINFO = "getAssetInfo";

    public static final String FUNC_GETETHKEY = "getEthKey";

    public static final String FUNC_GETQUANTUM = "getQuantum";

    public static final String FUNC_GETWITHDRAWALBALANCE = "getWithdrawalBalance";

    public static final String FUNC_ISFROZEN = "isFrozen";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final String FUNC_WITHDRAWANDMINT = "withdrawAndMint";

    public static final String FUNC_WITHDRAWNFT = "withdrawNft";

    public static final String FUNC_WITHDRAWWITHTOKENID = "withdrawWithTokenId";

    public static final Event LOGMINTWITHDRAWALPERFORMED_EVENT = new Event("LogMintWithdrawalPerformed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGNFTWITHDRAWALPERFORMED_EVENT = new Event("LogNftWithdrawalPerformed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event LOGWITHDRAWALPERFORMED_EVENT = new Event("LogWithdrawalPerformed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event LOGWITHDRAWALWITHTOKENIDPERFORMED_EVENT = new Event("LogWithdrawalWithTokenIdPerformed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>() {}));
    ;

    @Deprecated
    protected Withdrawals(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Withdrawals(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Withdrawals(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Withdrawals(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<LogMintWithdrawalPerformedEventResponse> getLogMintWithdrawalPerformedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGMINTWITHDRAWALPERFORMED_EVENT, transactionReceipt);
        ArrayList<LogMintWithdrawalPerformedEventResponse> responses = new ArrayList<LogMintWithdrawalPerformedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogMintWithdrawalPerformedEventResponse typedResponse = new LogMintWithdrawalPerformedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ownerKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogMintWithdrawalPerformedEventResponse> logMintWithdrawalPerformedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogMintWithdrawalPerformedEventResponse>() {
            @Override
            public LogMintWithdrawalPerformedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGMINTWITHDRAWALPERFORMED_EVENT, log);
                LogMintWithdrawalPerformedEventResponse typedResponse = new LogMintWithdrawalPerformedEventResponse();
                typedResponse.log = log;
                typedResponse.ownerKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogMintWithdrawalPerformedEventResponse> logMintWithdrawalPerformedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGMINTWITHDRAWALPERFORMED_EVENT));
        return logMintWithdrawalPerformedEventFlowable(filter);
    }

    public static List<LogNftWithdrawalPerformedEventResponse> getLogNftWithdrawalPerformedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGNFTWITHDRAWALPERFORMED_EVENT, transactionReceipt);
        ArrayList<LogNftWithdrawalPerformedEventResponse> responses = new ArrayList<LogNftWithdrawalPerformedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogNftWithdrawalPerformedEventResponse typedResponse = new LogNftWithdrawalPerformedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ownerKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogNftWithdrawalPerformedEventResponse> logNftWithdrawalPerformedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogNftWithdrawalPerformedEventResponse>() {
            @Override
            public LogNftWithdrawalPerformedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGNFTWITHDRAWALPERFORMED_EVENT, log);
                LogNftWithdrawalPerformedEventResponse typedResponse = new LogNftWithdrawalPerformedEventResponse();
                typedResponse.log = log;
                typedResponse.ownerKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogNftWithdrawalPerformedEventResponse> logNftWithdrawalPerformedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGNFTWITHDRAWALPERFORMED_EVENT));
        return logNftWithdrawalPerformedEventFlowable(filter);
    }

    public static List<LogWithdrawalPerformedEventResponse> getLogWithdrawalPerformedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGWITHDRAWALPERFORMED_EVENT, transactionReceipt);
        ArrayList<LogWithdrawalPerformedEventResponse> responses = new ArrayList<LogWithdrawalPerformedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogWithdrawalPerformedEventResponse typedResponse = new LogWithdrawalPerformedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ownerKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogWithdrawalPerformedEventResponse> logWithdrawalPerformedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogWithdrawalPerformedEventResponse>() {
            @Override
            public LogWithdrawalPerformedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGWITHDRAWALPERFORMED_EVENT, log);
                LogWithdrawalPerformedEventResponse typedResponse = new LogWithdrawalPerformedEventResponse();
                typedResponse.log = log;
                typedResponse.ownerKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogWithdrawalPerformedEventResponse> logWithdrawalPerformedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGWITHDRAWALPERFORMED_EVENT));
        return logWithdrawalPerformedEventFlowable(filter);
    }

    public static List<LogWithdrawalWithTokenIdPerformedEventResponse> getLogWithdrawalWithTokenIdPerformedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGWITHDRAWALWITHTOKENIDPERFORMED_EVENT, transactionReceipt);
        ArrayList<LogWithdrawalWithTokenIdPerformedEventResponse> responses = new ArrayList<LogWithdrawalWithTokenIdPerformedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogWithdrawalWithTokenIdPerformedEventResponse typedResponse = new LogWithdrawalWithTokenIdPerformedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.ownerKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(6).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogWithdrawalWithTokenIdPerformedEventResponse> logWithdrawalWithTokenIdPerformedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogWithdrawalWithTokenIdPerformedEventResponse>() {
            @Override
            public LogWithdrawalWithTokenIdPerformedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGWITHDRAWALWITHTOKENIDPERFORMED_EVENT, log);
                LogWithdrawalWithTokenIdPerformedEventResponse typedResponse = new LogWithdrawalWithTokenIdPerformedEventResponse();
                typedResponse.log = log;
                typedResponse.ownerKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
                typedResponse.recipient = (String) eventValues.getNonIndexedValues().get(6).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogWithdrawalWithTokenIdPerformedEventResponse> logWithdrawalWithTokenIdPerformedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGWITHDRAWALWITHTOKENIDPERFORMED_EVENT));
        return logWithdrawalWithTokenIdPerformedEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> calculateAssetIdWithTokenId(BigInteger assetType, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CALCULATEASSETIDWITHTOKENID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> calculateMintableAssetId(BigInteger assetType, byte[] mintingBlob) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_CALCULATEMINTABLEASSETID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.DynamicBytes(mintingBlob)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> getAssetInfo(BigInteger assetType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETASSETINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(assetType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> getEthKey(BigInteger ownerKey) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETETHKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ownerKey)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getQuantum(BigInteger presumedAssetType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETQUANTUM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(presumedAssetType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getWithdrawalBalance(BigInteger ownerKey, BigInteger assetId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETWITHDRAWALBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ownerKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> isFrozen() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISFROZEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw(BigInteger ownerKey, BigInteger assetType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ownerKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawAndMint(BigInteger ownerKey, BigInteger assetType, byte[] mintingBlob) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAWANDMINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ownerKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.DynamicBytes(mintingBlob)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawNft(BigInteger ownerKey, BigInteger assetType, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAWNFT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ownerKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> withdrawWithTokenId(BigInteger ownerKey, BigInteger assetType, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAWWITHTOKENID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ownerKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Withdrawals load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Withdrawals(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Withdrawals load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Withdrawals(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Withdrawals load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Withdrawals(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Withdrawals load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Withdrawals(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class LogMintWithdrawalPerformedEventResponse extends BaseEventResponse {
        public BigInteger ownerKey;

        public BigInteger assetType;

        public BigInteger nonQuantizedAmount;

        public BigInteger quantizedAmount;

        public BigInteger assetId;
    }

    public static class LogNftWithdrawalPerformedEventResponse extends BaseEventResponse {
        public BigInteger ownerKey;

        public BigInteger assetType;

        public BigInteger tokenId;

        public BigInteger assetId;

        public String recipient;
    }

    public static class LogWithdrawalPerformedEventResponse extends BaseEventResponse {
        public BigInteger ownerKey;

        public BigInteger assetType;

        public BigInteger nonQuantizedAmount;

        public BigInteger quantizedAmount;

        public String recipient;
    }

    public static class LogWithdrawalWithTokenIdPerformedEventResponse extends BaseEventResponse {
        public BigInteger ownerKey;

        public BigInteger assetType;

        public BigInteger tokenId;

        public BigInteger assetId;

        public BigInteger nonQuantizedAmount;

        public BigInteger quantizedAmount;

        public String recipient;
    }
}
