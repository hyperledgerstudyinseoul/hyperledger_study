package com.blockchain.board.service;

import java.net.MalformedURLException;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.NetworkConfig.CAInfo;
import org.hyperledger.fabric.sdk.NetworkConfig.OrgInfo;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InfoException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;

import com.blockchain.board.common.vo.BoardUser;

public interface BoardService {

	void boardInsert();

	BoardUser getBoardUser(OrgInfo clientOrg, CAInfo caInfo, NetworkConfig networkConfig) throws EnrollmentException, InvalidArgumentException, MalformedURLException, InfoException;

	ProposalResponse executeChaincode(HFClient client, Channel channel,String ccName, String funcName, String[] args,boolean invoke) throws ProposalException, org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
	
		
}
