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
public class ReddioDeployHelper extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_COUNTER = "counter";

    public static final String FUNC_DEPLOYERC20ANDREGISTER = "deployERC20AndRegister";

    public static final String FUNC_DEPLOYERC721ANDREGISTER = "deployERC721AndRegister";

    public static final String FUNC_REGISTERPROXY = "registerProxy";

    public static final String FUNC_REGISTERTOKEN = "registerToken";

    public static final Event NEWERC20_EVENT = new Event("NewERC20", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    public static final Event NEWERC721_EVENT = new Event("NewERC721", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}));
    ;

    @Deprecated
    protected ReddioDeployHelper(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected ReddioDeployHelper(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected ReddioDeployHelper(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected ReddioDeployHelper(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<NewERC20EventResponse> getNewERC20Events(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(NEWERC20_EVENT, transactionReceipt);
        ArrayList<NewERC20EventResponse> responses = new ArrayList<NewERC20EventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewERC20EventResponse typedResponse = new NewERC20EventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.deployer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.token = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewERC20EventResponse> newERC20EventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, NewERC20EventResponse>() {
            @Override
            public NewERC20EventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWERC20_EVENT, log);
                NewERC20EventResponse typedResponse = new NewERC20EventResponse();
                typedResponse.log = log;
                typedResponse.deployer = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.token = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewERC20EventResponse> newERC20EventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWERC20_EVENT));
        return newERC20EventFlowable(filter);
    }

    public static List<NewERC721EventResponse> getNewERC721Events(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(NEWERC721_EVENT, transactionReceipt);
        ArrayList<NewERC721EventResponse> responses = new ArrayList<NewERC721EventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NewERC721EventResponse typedResponse = new NewERC721EventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.deployer = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.token = (String) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NewERC721EventResponse> newERC721EventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, NewERC721EventResponse>() {
            @Override
            public NewERC721EventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NEWERC721_EVENT, log);
                NewERC721EventResponse typedResponse = new NewERC721EventResponse();
                typedResponse.log = log;
                typedResponse.deployer = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.token = (String) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NewERC721EventResponse> newERC721EventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NEWERC721_EVENT));
        return newERC721EventFlowable(filter);
    }

    public RemoteFunctionCall<BigInteger> counter(String deployer) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_COUNTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, deployer)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> deployERC20AndRegister(String name_, String symbol_, BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPLOYERC20ANDREGISTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name_), 
                new org.web3j.abi.datatypes.Utf8String(symbol_), 
                new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> deployERC721AndRegister(String name_, String symbol_, String baseURI_) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPLOYERC721ANDREGISTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name_), 
                new org.web3j.abi.datatypes.Utf8String(symbol_), 
                new org.web3j.abi.datatypes.Utf8String(baseURI_), 
                new org.web3j.abi.datatypes.generated.Uint8(BigInteger.ONE)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> deployERC721MAndRegister(String name_, String symbol_, String baseURI_) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPLOYERC721ANDREGISTER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name_),
                        new org.web3j.abi.datatypes.Utf8String(symbol_),
                        new org.web3j.abi.datatypes.Utf8String(baseURI_),
                        new org.web3j.abi.datatypes.generated.Uint8(BigInteger.TWO)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> deployERC721MCAndRegister(String name_, String symbol_) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_DEPLOYERC721ANDREGISTER,
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(name_),
                        new org.web3j.abi.datatypes.Utf8String(symbol_),
                        new org.web3j.abi.datatypes.Utf8String(""),
                        new org.web3j.abi.datatypes.generated.Uint8(BigInteger.valueOf(3))),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> registerProxy() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_REGISTERPROXY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerToken(String token, BigInteger asset) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REGISTERTOKEN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, token), 
                new org.web3j.abi.datatypes.generated.Uint8(asset)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static ReddioDeployHelper load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new ReddioDeployHelper(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static ReddioDeployHelper load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new ReddioDeployHelper(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static ReddioDeployHelper load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new ReddioDeployHelper(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static ReddioDeployHelper load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new ReddioDeployHelper(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class NewERC20EventResponse extends BaseEventResponse {
        public String deployer;

        public String token;
    }

    public static class NewERC721EventResponse extends BaseEventResponse {
        public String deployer;

        public String token;
    }
}
