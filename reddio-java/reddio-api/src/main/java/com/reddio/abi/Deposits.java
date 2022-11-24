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
public class Deposits extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_DEPOSIT_CANCEL_DELAY = "DEPOSIT_CANCEL_DELAY";

    public static final String FUNC_FREEZE_GRACE_PERIOD = "FREEZE_GRACE_PERIOD";

    public static final String FUNC_MAX_FORCED_ACTIONS_REQS_PER_BLOCK = "MAX_FORCED_ACTIONS_REQS_PER_BLOCK";

    public static final String FUNC_MAX_VERIFIER_COUNT = "MAX_VERIFIER_COUNT";

    public static final String FUNC_UNFREEZE_DELAY = "UNFREEZE_DELAY";

    public static final String FUNC_VERIFIER_REMOVAL_DELAY = "VERIFIER_REMOVAL_DELAY";

    public static final String FUNC_CALCULATEASSETIDWITHTOKENID = "calculateAssetIdWithTokenId";

    public static final String FUNC_CALCULATEMINTABLEASSETID = "calculateMintableAssetId";

    public static final String FUNC_deposit = "deposit";

    public static final String FUNC_DEPOSITCANCEL = "depositCancel";

    public static final String FUNC_DEPOSITERC1155 = "depositERC1155";

    public static final String FUNC_DEPOSITERC20 = "depositERC20";

    public static final String FUNC_DEPOSITETH = "depositEth";

    public static final String FUNC_DEPOSITNFT = "depositNft";

    public static final String FUNC_DEPOSITNFTRECLAIM = "depositNftReclaim";

    public static final String FUNC_DEPOSITRECLAIM = "depositReclaim";

    public static final String FUNC_DEPOSITWITHTOKENID = "depositWithTokenId";

    public static final String FUNC_DEPOSITWITHTOKENIDRECLAIM = "depositWithTokenIdReclaim";

    public static final String FUNC_GETASSETINFO = "getAssetInfo";

    public static final String FUNC_GETCANCELLATIONREQUEST = "getCancellationRequest";

    public static final String FUNC_GETDEPOSITBALANCE = "getDepositBalance";

    public static final String FUNC_GETETHKEY = "getEthKey";

    public static final String FUNC_GETQUANTIZEDDEPOSITBALANCE = "getQuantizedDepositBalance";

    public static final String FUNC_GETQUANTUM = "getQuantum";

    public static final String FUNC_ISFROZEN = "isFrozen";

    public static final Event LOGDEPOSIT_EVENT = new Event("LogDeposit", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGDEPOSITCANCEL_EVENT = new Event("LogDepositCancel", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGDEPOSITCANCELRECLAIMED_EVENT = new Event("LogDepositCancelReclaimed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGDEPOSITNFTCANCELRECLAIMED_EVENT = new Event("LogDepositNftCancelReclaimed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGDEPOSITWITHTOKENID_EVENT = new Event("LogDepositWithTokenId", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGDEPOSITWITHTOKENIDCANCELRECLAIMED_EVENT = new Event("LogDepositWithTokenIdCancelReclaimed", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event LOGNFTDEPOSIT_EVENT = new Event("LogNftDeposit", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected Deposits(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Deposits(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Deposits(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Deposits(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<LogDepositEventResponse> getLogDepositEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGDEPOSIT_EVENT, transactionReceipt);
        ArrayList<LogDepositEventResponse> responses = new ArrayList<LogDepositEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogDepositEventResponse typedResponse = new LogDepositEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.depositorEthKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogDepositEventResponse> logDepositEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogDepositEventResponse>() {
            @Override
            public LogDepositEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGDEPOSIT_EVENT, log);
                LogDepositEventResponse typedResponse = new LogDepositEventResponse();
                typedResponse.log = log;
                typedResponse.depositorEthKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogDepositEventResponse> logDepositEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGDEPOSIT_EVENT));
        return logDepositEventFlowable(filter);
    }

    public static List<LogDepositCancelEventResponse> getLogDepositCancelEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGDEPOSITCANCEL_EVENT, transactionReceipt);
        ArrayList<LogDepositCancelEventResponse> responses = new ArrayList<LogDepositCancelEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogDepositCancelEventResponse typedResponse = new LogDepositCancelEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogDepositCancelEventResponse> logDepositCancelEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogDepositCancelEventResponse>() {
            @Override
            public LogDepositCancelEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGDEPOSITCANCEL_EVENT, log);
                LogDepositCancelEventResponse typedResponse = new LogDepositCancelEventResponse();
                typedResponse.log = log;
                typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogDepositCancelEventResponse> logDepositCancelEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGDEPOSITCANCEL_EVENT));
        return logDepositCancelEventFlowable(filter);
    }

    public static List<LogDepositCancelReclaimedEventResponse> getLogDepositCancelReclaimedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGDEPOSITCANCELRECLAIMED_EVENT, transactionReceipt);
        ArrayList<LogDepositCancelReclaimedEventResponse> responses = new ArrayList<LogDepositCancelReclaimedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogDepositCancelReclaimedEventResponse typedResponse = new LogDepositCancelReclaimedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogDepositCancelReclaimedEventResponse> logDepositCancelReclaimedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogDepositCancelReclaimedEventResponse>() {
            @Override
            public LogDepositCancelReclaimedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGDEPOSITCANCELRECLAIMED_EVENT, log);
                LogDepositCancelReclaimedEventResponse typedResponse = new LogDepositCancelReclaimedEventResponse();
                typedResponse.log = log;
                typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogDepositCancelReclaimedEventResponse> logDepositCancelReclaimedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGDEPOSITCANCELRECLAIMED_EVENT));
        return logDepositCancelReclaimedEventFlowable(filter);
    }

    public static List<LogDepositNftCancelReclaimedEventResponse> getLogDepositNftCancelReclaimedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGDEPOSITNFTCANCELRECLAIMED_EVENT, transactionReceipt);
        ArrayList<LogDepositNftCancelReclaimedEventResponse> responses = new ArrayList<LogDepositNftCancelReclaimedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogDepositNftCancelReclaimedEventResponse typedResponse = new LogDepositNftCancelReclaimedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogDepositNftCancelReclaimedEventResponse> logDepositNftCancelReclaimedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogDepositNftCancelReclaimedEventResponse>() {
            @Override
            public LogDepositNftCancelReclaimedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGDEPOSITNFTCANCELRECLAIMED_EVENT, log);
                LogDepositNftCancelReclaimedEventResponse typedResponse = new LogDepositNftCancelReclaimedEventResponse();
                typedResponse.log = log;
                typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogDepositNftCancelReclaimedEventResponse> logDepositNftCancelReclaimedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGDEPOSITNFTCANCELRECLAIMED_EVENT));
        return logDepositNftCancelReclaimedEventFlowable(filter);
    }

    public static List<LogDepositWithTokenIdEventResponse> getLogDepositWithTokenIdEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGDEPOSITWITHTOKENID_EVENT, transactionReceipt);
        ArrayList<LogDepositWithTokenIdEventResponse> responses = new ArrayList<LogDepositWithTokenIdEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogDepositWithTokenIdEventResponse typedResponse = new LogDepositWithTokenIdEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.depositorEthKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
            typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(7).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogDepositWithTokenIdEventResponse> logDepositWithTokenIdEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogDepositWithTokenIdEventResponse>() {
            @Override
            public LogDepositWithTokenIdEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGDEPOSITWITHTOKENID_EVENT, log);
                LogDepositWithTokenIdEventResponse typedResponse = new LogDepositWithTokenIdEventResponse();
                typedResponse.log = log;
                typedResponse.depositorEthKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
                typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
                typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(7).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogDepositWithTokenIdEventResponse> logDepositWithTokenIdEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGDEPOSITWITHTOKENID_EVENT));
        return logDepositWithTokenIdEventFlowable(filter);
    }

    public static List<LogDepositWithTokenIdCancelReclaimedEventResponse> getLogDepositWithTokenIdCancelReclaimedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGDEPOSITWITHTOKENIDCANCELRECLAIMED_EVENT, transactionReceipt);
        ArrayList<LogDepositWithTokenIdCancelReclaimedEventResponse> responses = new ArrayList<LogDepositWithTokenIdCancelReclaimedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogDepositWithTokenIdCancelReclaimedEventResponse typedResponse = new LogDepositWithTokenIdCancelReclaimedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogDepositWithTokenIdCancelReclaimedEventResponse> logDepositWithTokenIdCancelReclaimedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogDepositWithTokenIdCancelReclaimedEventResponse>() {
            @Override
            public LogDepositWithTokenIdCancelReclaimedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGDEPOSITWITHTOKENIDCANCELRECLAIMED_EVENT, log);
                LogDepositWithTokenIdCancelReclaimedEventResponse typedResponse = new LogDepositWithTokenIdCancelReclaimedEventResponse();
                typedResponse.log = log;
                typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.nonQuantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
                typedResponse.quantizedAmount = (BigInteger) eventValues.getNonIndexedValues().get(6).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogDepositWithTokenIdCancelReclaimedEventResponse> logDepositWithTokenIdCancelReclaimedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGDEPOSITWITHTOKENIDCANCELRECLAIMED_EVENT));
        return logDepositWithTokenIdCancelReclaimedEventFlowable(filter);
    }

    public static List<LogNftDepositEventResponse> getLogNftDepositEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(LOGNFTDEPOSIT_EVENT, transactionReceipt);
        ArrayList<LogNftDepositEventResponse> responses = new ArrayList<LogNftDepositEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            LogNftDepositEventResponse typedResponse = new LogNftDepositEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.depositorEthKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<LogNftDepositEventResponse> logNftDepositEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, LogNftDepositEventResponse>() {
            @Override
            public LogNftDepositEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(LOGNFTDEPOSIT_EVENT, log);
                LogNftDepositEventResponse typedResponse = new LogNftDepositEventResponse();
                typedResponse.log = log;
                typedResponse.depositorEthKey = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.starkKey = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.vaultId = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.assetType = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.assetId = (BigInteger) eventValues.getNonIndexedValues().get(5).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<LogNftDepositEventResponse> logNftDepositEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(LOGNFTDEPOSIT_EVENT));
        return logNftDepositEventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> DEPOSIT_CANCEL_DELAY() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DEPOSIT_CANCEL_DELAY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> FREEZE_GRACE_PERIOD() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FREEZE_GRACE_PERIOD, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MAX_FORCED_ACTIONS_REQS_PER_BLOCK() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MAX_FORCED_ACTIONS_REQS_PER_BLOCK, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> MAX_VERIFIER_COUNT() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MAX_VERIFIER_COUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> UNFREEZE_DELAY() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_UNFREEZE_DELAY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> VERIFIER_REMOVAL_DELAY() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_VERIFIER_REMOVAL_DELAY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
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

    public RemoteFunctionCall<TransactionReceipt> deposit(BigInteger starkKey, BigInteger assetType, BigInteger vaultId, BigInteger quantizedAmount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_deposit, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId), 
                new org.web3j.abi.datatypes.generated.Uint256(quantizedAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> depositCancel(BigInteger starkKey, BigInteger assetId, BigInteger vaultId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITCANCEL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetId), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> depositERC1155(BigInteger starkKey, BigInteger assetType, BigInteger tokenId, BigInteger vaultId, BigInteger quantizedAmount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITERC1155, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId), 
                new org.web3j.abi.datatypes.generated.Uint256(quantizedAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> depositERC20(BigInteger starkKey, BigInteger assetType, BigInteger vaultId, BigInteger quantizedAmount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITERC20, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId), 
                new org.web3j.abi.datatypes.generated.Uint256(quantizedAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> depositEth(BigInteger starkKey, BigInteger assetType, BigInteger vaultId, BigInteger weiValue) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITETH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> depositNft(BigInteger starkKey, BigInteger assetType, BigInteger vaultId, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITNFT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> depositNftReclaim(BigInteger starkKey, BigInteger assetType, BigInteger vaultId, BigInteger tokenId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITNFTRECLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> depositReclaim(BigInteger starkKey, BigInteger assetType, BigInteger vaultId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITRECLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> depositWithTokenId(BigInteger starkKey, BigInteger assetType, BigInteger tokenId, BigInteger vaultId, BigInteger quantizedAmount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITWITHTOKENID, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId), 
                new org.web3j.abi.datatypes.generated.Uint256(quantizedAmount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> depositWithTokenIdReclaim(BigInteger starkKey, BigInteger assetType, BigInteger tokenId, BigInteger vaultId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPOSITWITHTOKENIDRECLAIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetType), 
                new org.web3j.abi.datatypes.generated.Uint256(tokenId), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<byte[]> getAssetInfo(BigInteger assetType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETASSETINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(assetType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> getCancellationRequest(BigInteger starkKey, BigInteger assetId, BigInteger vaultId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCANCELLATIONREQUEST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetId), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getDepositBalance(BigInteger starkKey, BigInteger assetId, BigInteger vaultId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETDEPOSITBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetId), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getEthKey(BigInteger ownerKey) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETETHKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(ownerKey)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getQuantizedDepositBalance(BigInteger starkKey, BigInteger assetId, BigInteger vaultId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETQUANTIZEDDEPOSITBALANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(starkKey), 
                new org.web3j.abi.datatypes.generated.Uint256(assetId), 
                new org.web3j.abi.datatypes.generated.Uint256(vaultId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getQuantum(BigInteger presumedAssetType) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETQUANTUM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(presumedAssetType)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> isFrozen() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ISFROZEN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static Deposits load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Deposits(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Deposits load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Deposits(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Deposits load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Deposits(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Deposits load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Deposits(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class LogDepositEventResponse extends BaseEventResponse {
        public String depositorEthKey;

        public BigInteger starkKey;

        public BigInteger vaultId;

        public BigInteger assetType;

        public BigInteger nonQuantizedAmount;

        public BigInteger quantizedAmount;
    }

    public static class LogDepositCancelEventResponse extends BaseEventResponse {
        public BigInteger starkKey;

        public BigInteger vaultId;

        public BigInteger assetId;
    }

    public static class LogDepositCancelReclaimedEventResponse extends BaseEventResponse {
        public BigInteger starkKey;

        public BigInteger vaultId;

        public BigInteger assetType;

        public BigInteger nonQuantizedAmount;

        public BigInteger quantizedAmount;
    }

    public static class LogDepositNftCancelReclaimedEventResponse extends BaseEventResponse {
        public BigInteger starkKey;

        public BigInteger vaultId;

        public BigInteger assetType;

        public BigInteger tokenId;

        public BigInteger assetId;
    }

    public static class LogDepositWithTokenIdEventResponse extends BaseEventResponse {
        public String depositorEthKey;

        public BigInteger starkKey;

        public BigInteger vaultId;

        public BigInteger assetType;

        public BigInteger tokenId;

        public BigInteger assetId;

        public BigInteger nonQuantizedAmount;

        public BigInteger quantizedAmount;
    }

    public static class LogDepositWithTokenIdCancelReclaimedEventResponse extends BaseEventResponse {
        public BigInteger starkKey;

        public BigInteger vaultId;

        public BigInteger assetType;

        public BigInteger tokenId;

        public BigInteger assetId;

        public BigInteger nonQuantizedAmount;

        public BigInteger quantizedAmount;
    }

    public static class LogNftDepositEventResponse extends BaseEventResponse {
        public String depositorEthKey;

        public BigInteger starkKey;

        public BigInteger vaultId;

        public BigInteger assetType;

        public BigInteger tokenId;

        public BigInteger assetId;
    }
}
